import com.my.home.beans.TestLogIdentifier;
import com.my.home.factory.SpringBeanFactory;
import com.my.home.log.beans.LogNode;
import com.my.home.plugin.IPluginFactory;
import com.my.home.plugin.IPluginProcessor;
import com.my.home.plugin.IPluginStorage;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.plugin.model.PluginType;
import com.my.home.processor.ILogStorage;
import com.my.home.processor.ILogStorageCommand;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogStorageContext;
import com.my.home.storage.LogStorageImpl;
import com.my.home.storage.mongo.commands.FindNodesCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class Runner
{
    private static final String LOG_FOLDER = "1000/src/main/resources/logToTest/";
    private static final String PLUGIN_FROM_FOLDER = "1000/src/main/resources/plugins/from/";
    private static final String PLUGIN_TO_FOLDER = "1000/src/main/resources/plugins/to/";
    private static final String PLUGIN_FILE_NAME = "Plugin1-1.0-SNAPSHOT.jar";
    private static final String LOG_FILE_NAME_TEMPLATE = "matrixtdp_0%s.log";
    private static final String THREADS_FILE = "ListOfThreads";
    private static final int FILES_NUMBER = 6;
    public static void main(String[] args)
    {
        //testLogStorage();
        testPluginStorage();
    }

    private static void testPluginStorage()
    {
        IPluginFactory factory = (IPluginFactory) SpringBeanFactory.getInstance().getBean("PluginFactory");
        IPluginStorage storage = (IPluginStorage) SpringBeanFactory.getInstance().getBean("PluginStorage");

        factory.savePlugin(new File(PLUGIN_FROM_FOLDER + PLUGIN_FILE_NAME));
        List<PluginToStore> plugs = storage.get();
        PluginToStore pluginToTake = plugs.get(2);
        long time = System.currentTimeMillis();
        if (pluginToTake.getType() == PluginType.PROCESSOR)
        {
            IPluginProcessor processor = factory.getPlugin(pluginToTake);
            ILogStorageContext context = (ILogStorageContext)SpringBeanFactory.getInstance().getBean("StorageContext");
            ILogStorage logStorage = new LogStorageImpl();
            logStorage.setStorageContext(context);
            ILogIdentifier identifier = new TestLogIdentifier("randomKey");


            ILogStorageCommand<LogNode> command = new FindNodesCommand();
            LogNode node1 = new LogNode();
            LogNode node2 = new LogNode();

            node1.setThread("WorkManager(2)-43");
            node2.setThread("WorkManager(2)-84");
            //command.setData(node1, node2);
            command.setData(getAllThreads());
            Iterator<LogNode> iter = logStorage.getIterator(identifier, command);
            while (iter.hasNext())
            {
                processor.process(iter.next());
                //System.out.println(iter.next().getMessage());
            }
            System.out.println(processor.getResult());
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Time to save : " + time + " ms.");
    }
    private static void testLogStorage()
    {
        ILogStorageContext context = (ILogStorageContext)SpringBeanFactory.getInstance().getBean("StorageContext");
        ILogStorage storage = new LogStorageImpl();
        storage.setStorageContext(context);
        ILogIdentifier identifier = new TestLogIdentifier("randomKey");

        //  Test of uploading data into DB
        long time = System.currentTimeMillis();
        //uploadData(storage, identifier);
        time = System.currentTimeMillis() - time;
        System.out.println("Time to save : " + time + " ms.");

        // Test of retrieving data
        time = System.currentTimeMillis();
        retrieveData(storage, identifier);
        time = System.currentTimeMillis() - time;
        System.out.println("Time to retrieve : " + time + " ms.");
    }
    private static void uploadData(ILogStorage storage, ILogIdentifier identifier)
    {
        List<File> files = getLogFiles();
        files.forEach(path -> System.out.println(path.getAbsolutePath() + ": exist: " + path.exists()));
        storage.process(identifier, files);
    }
    private static void retrieveData(ILogStorage storage, ILogIdentifier identifier)
    {
        ILogStorageCommand<LogNode> command = new FindNodesCommand();
        LogNode node1 = new LogNode();
        LogNode node2 = new LogNode();

        //node1.setThread("WorkManager(2)-43");
        //node2.setThread("WorkManager(2)-84");
        command.setData(getAllThreads());
        Iterator<LogNode> iter = storage.getIterator(identifier, command);
        while (iter.hasNext())
        {
            System.out.println(iter.next().getMessage());
        }
        //String log = storage.getLog(identifier, command);
        //System.out.println(log);
    }
    private static List<File> getLogFiles()
    {
        List<File> out = new ArrayList<>(FILES_NUMBER);
        for (int i = 1; i <= FILES_NUMBER; i++)
        {
            out.add(new File(String.format(LOG_FOLDER + LOG_FILE_NAME_TEMPLATE, i)));
        }
        return out;
    }
    private static LogNode[] getAllThreads()
    {
        try {
            File f = new File(LOG_FOLDER + THREADS_FILE);
            List<LogNode> nodes = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                LogNode node = new LogNode();
                node.setThread(line);
                nodes.add(node);
            }
            LogNode[] out = new LogNode[nodes.size()];
            for (int i = 0; i< nodes.size(); i++)
            {
                out[i] = nodes.get(i);
            }
            return out;
        }
        catch (Throwable e)
        {
             return new LogNode[0];
        }


    }
}
