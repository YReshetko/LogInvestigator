import com.my.home.beans.TestLogIdentifier;
import com.my.home.factory.SpringBeanFactory;
import com.my.home.processor.ILogStorage;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogStorageContext;
import com.my.home.storage.LogStorageImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Runner
{
    private static final String LOG_FOLDER = "1000/src/main/resources/logToTest/";
    private static final String LOG_FILE_NAME_TEMPLATE = "matrixtdp_0%s.log";
    private static final int FILES_NUMBER = 6;
    public static void main(String[] args)
    {
        long time = System.currentTimeMillis();
        ILogStorageContext context = (ILogStorageContext)SpringBeanFactory.getInstance().getBean("StorageContext");
        ILogStorage storage = new LogStorageImpl();
        storage.setStorageContext(context);

        List<File> files = getLogFiles();
        files.forEach(path -> System.out.println(path.getAbsolutePath() + ": exist: " + path.exists()));
        ILogIdentifier identifier = new TestLogIdentifier("randomKey");

        storage.process(identifier, files);

        time = System.currentTimeMillis() - time;
        System.out.println("Time: " + time + " ms.");
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
}
