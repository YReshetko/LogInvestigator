package com.my.home.storage;

import com.google.gson.Gson;
import com.my.home.base.DataToTest;
import com.my.home.base.TestBase;
import com.my.home.base.TestUtils;
import com.my.home.log.LogNodeParser;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Test for caching meta inf about log
 */
public class LogMetaInfCacheTest extends TestBase
{
    private static final String INPUT_SUB_DIR = "input/parser/";
    private LogMetaInfCache m_toTest;

    @Before
    public void setUp()
    {
        m_toTest = new LogMetaInfCache();
    }
    @Test
    public void testCaching() throws Exception
    {
        LogNodeParser parser = new LogNodeParser();
        DataToTest settings = initParserWithFile(parser, "TestCase1");
        Long index = 0L;
        for (String logLine : settings.getStringsToTest())
        {
            index++;
            LogNode node = parser.parse(logLine);
            node.setId(index);
            m_toTest.cacheMetaInf(node);
        }

        List<ThreadDescriptor> descriptors = m_toTest.getThreadDescriptors();
        ThreadsInfo info = m_toTest.getThreadsInfo();
        
        Assert.assertEquals(2, descriptors.size());
        Assert.assertEquals(2, info.getThreads().size());
        Assert.assertEquals("WorkManager(2)-71", descriptors.get(0).getName());
        Assert.assertEquals("WorkManager(2)-43", descriptors.get(1).getName());
        Assert.assertEquals(2, descriptors.get(1).getNodesNumbers().size());

        Assert.assertEquals("WorkManager(2)-43", info.getThreads().get(0).getThreadName());
        Assert.assertEquals("WorkManager(2)-71", info.getThreads().get(1).getThreadName());
    }

    /**
     * Init parser with some file
     * @param fileName - file to setup
     * @throws java.io.IOException - unexpected exception
     */
    private DataToTest initParserWithFile(LogNodeParser parser, String fileName) throws IOException
    {
        String testCase1 = loadFile(INPUT_SUB_DIR + fileName);
        Gson gson = new Gson();
        DataToTest settings = (DataToTest) gson.fromJson(testCase1, DataToTest.class);
        TestUtils.setUpParser(parser, settings);
        return settings;
    }
}
