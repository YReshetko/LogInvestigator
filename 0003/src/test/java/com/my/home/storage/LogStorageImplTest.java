package com.my.home.storage;

import com.my.home.base.DataToTest;
import com.my.home.base.TestBase;
import com.my.home.base.TestUtils;
import com.my.home.log.LogNodeParser;
import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.processor.ILogProgress;
import com.my.home.processor.ILogStorage;
import com.my.home.processor.ILogStorageCommand;
import com.my.home.util.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Test of log storage implementation (save/retrieve log nodes)
 */
public class LogStorageImplTest extends TestBase
{
    private static final String BASE_PATH = "input/storage/saving/";
    private ILogStorage m_toTest;
    private ILogStorageContext context;

    /**
     * Setup method
     * @throws Exception - unexpected exception
     */
    @Before
    public void setUp() throws Exception
    {
        ILogNodeParser parser = new LogNodeParser();
        String testCase1 = loadFile( "input/parser/TestCase1");
        DataToTest settings = JsonUtils.getObject(testCase1, DataToTest.class);
        TestUtils.setUpParser((LogNodeParser)parser, settings);
        context = new StorageContextTest(parser);
        m_toTest = new LogStorageImpl();
        m_toTest.setStorageContext(context);
    }

    /**
     * Save log files and check if it pass though saver
     */
    @Test
    public void testSaving() throws ExecutionException, InterruptedException
    {
        List<File> files = getFiles(Arrays.asList(
                BASE_PATH + "File_1",
                BASE_PATH + "File_2",
                BASE_PATH + "File_3",
                BASE_PATH + "File_4"));
        Future<ILogIdentifier> future = m_toTest.process(new IdentifierTest(), files);
        future.get();
        List<LogNode> nodes = ((TestSaver) context.getSaver()).nodes;
        Assert.assertTrue(nodes.size() > 0);
        for (int i = 1; i<nodes.size(); i++)
        {
            Assert.assertTrue(nodes.get(i).getLongDateTime().compareTo(nodes.get(i-1).getLongDateTime()) >= 0);
        }
    }

    /**
     * Save and retrieve log nodes
     */
    @Test
    public void testRetrieving() throws ExecutionException, InterruptedException
    {
        List<File> files = getFiles(Arrays.asList(
                BASE_PATH + "File_1",
                BASE_PATH + "File_2",
                BASE_PATH + "File_3",
                BASE_PATH + "File_4"));
        Future<ILogIdentifier> future = m_toTest.process(new IdentifierTest(), files);
        future.get();
        Iterator<LogNode> iter = m_toTest.getIterator(new IdentifierTest(), new TestLogCommand());
        Assert.assertTrue(iter.hasNext());
        int index = 0;
        while (iter.hasNext())
        {
            ++index;
            LogNode node = iter.next();
            Assert.assertNotNull(node);
        }
        Assert.assertTrue(index > 49);
    }

    /**
     * Save log and retrieve log nodes as text of log
     */
    @Test
    public void testLogUnParsing() throws ExecutionException, InterruptedException
    {
        List<File> files = getFiles(Arrays.asList(
                BASE_PATH + "File_1",
                BASE_PATH + "File_2",
                BASE_PATH + "File_3",
                BASE_PATH + "File_4"));
        Future<ILogIdentifier> future = m_toTest.process(new IdentifierTest(), files);
        future.get();
        String out = m_toTest.getLog(new IdentifierTest(), new TestLogCommand());
        Assert.assertNotNull(out);
        Assert.assertTrue(out.getBytes().length > 10000);
    }


    /**
     * Test implementation of log identifier
     */
    private class IdentifierTest implements ILogIdentifier
    {

        @Override
        public String getKey() {
            return "Test case";
        }

        @Override
        public LogFilesDescriptor getLogDescriptor() {
            return null;
        }

    }

    /**
     * Test implementation of storage context
     */
    private class StorageContextTest implements ILogStorageContext
    {
        ILogNodeParser parser;
        ILogSaver saver;
        ILogRetriever retriever;
        public StorageContextTest(ILogNodeParser parser)
        {
            this.parser = parser;
            saver = new TestSaver();
            retriever = new TestRetriever(((TestSaver)saver).nodes);
        }
        @Override
        public ILogNodeParser getParser() {
            return parser;
        }

        @Override
        public ILogSaver getSaver() {
            return saver;
        }

        @Override
        public ILogRetriever getRetriever() {
            return retriever;
        }

        @Override
        public ILogProgress getProgress() {
            return new ILogProgress() {
                @Override
                public void addTotalSize(long l) {

                }

                @Override
                public void setTotalSize(long l) {

                }

                @Override
                public void subtractSize(long l) {

                }

                @Override
                public double getProgress() {
                    return 0;
                }
            };
        }
    }

    /**
     * Test implementation of saver
     */
    private class TestSaver implements ILogSaver
    {

        List<LogNode> nodes = new ArrayList<>();
        List<ThreadDescriptor> descriptors = new ArrayList<>();
        ThreadsInfo threadsInfo;

        @Override
        public boolean saveNode(ILogIdentifier identifier, LogNode node)
        {
            nodes.add(node);
            return true;
        }

        @Override
        public boolean saveThreadsInfo(ILogIdentifier identifier, ThreadsInfo threadsInfo)
        {
            this.threadsInfo = threadsInfo;
            return true;
        }

        @Override
        public boolean saveThreadDescriptor(ILogIdentifier identifier, ThreadDescriptor descriptor)
        {
            descriptors.add(descriptor);
            return true;
        }

        @Override
        public boolean complete(ILogIdentifier identifier) {
            return true;
        }
    }

    /**
     * Test implementation of retriever
     */
    private class TestRetriever implements ILogRetriever
    {


        private List<LogNode> nodes;
        public TestRetriever(List<LogNode> nodes)
        {
            this.nodes = nodes;
        }
        @Override
        public <V> Iterator<V> get(ILogIdentifier identifier, ILogStorageCommand<V> viLogStorageCommand) {
            return (Iterator<V>) nodes.iterator();
        }

        @Override
        public void changeLog(ILogIdentifier identifier, ILogStorageCommand iLogStorageCommand) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    /**
     * Test implementation of command
     */
    private class TestLogCommand implements ILogStorageCommand<LogNode>
    {

        @Override
        public void setData(LogNode... logNodes) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setData(ILogIdentifier identifier, LogNode... logNodes) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setData(ILogIdentifier identifier) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public ILogIdentifier getIdentifier() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getCommand() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String sortBy() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
