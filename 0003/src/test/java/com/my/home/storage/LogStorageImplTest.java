package com.my.home.storage;

import com.google.gson.Gson;
import com.my.home.base.DataToTest;
import com.my.home.base.TestBase;
import com.my.home.base.TestUtils;
import com.my.home.log.LogNodeParser;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.processor.ILogStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 24.06.17
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class LogStorageImplTest extends TestBase
{
    private static final String BASE_PATH = "input/storage/saving/";
    private ILogStorage m_toTest;
    private ILogStorageContext context;

    @Before
    public void setUp() throws Exception
    {
        ILogNodeParser parser = new LogNodeParser();
        String testCase1 = loadFile( "input/parser/TestCase1");
        Gson gson = new Gson();
        DataToTest settings = (DataToTest) gson.fromJson(testCase1, DataToTest.class);
        TestUtils.setUpParser((LogNodeParser)parser, settings);
        context = new StorageContextTest(parser);
        m_toTest = new LogStorageImpl();
        m_toTest.setStorageContext(context);
    }

    @Test
    public void testSaving()
    {
        List<File> files = getFiles(Arrays.asList(
                BASE_PATH + "File_1",
                BASE_PATH + "File_2",
                BASE_PATH + "File_3",
                BASE_PATH + "File_4"));
        m_toTest.process(new IdentifierTest(), files);
        List<LogNode> nodes = ((TestSaver) context.getSaver()).nodes;
        Assert.assertTrue(nodes.size() > 0);
        for (int i = 1; i<nodes.size(); i++)
        {
            Assert.assertTrue(nodes.get(i).getLongDateTime().compareTo(nodes.get(i-1).getLongDateTime()) >= 0);
        }
    }

    private class IdentifierTest implements ILogIdentifier
    {

        @Override
        public String getKey() {
            return "Test case";
        }
    }
    private class StorageContextTest implements ILogStorageContext
    {
        ILogNodeParser parser;
        ILogSaver saver;
        ILogRetriever retriever;
        public StorageContextTest(ILogNodeParser parser)
        {
            this.parser = parser;
            saver = new TestSaver();
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
    }
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
        public boolean complete(ILogIdentifier identifier, List<File> files) {
            return true;
        }
    }
}
