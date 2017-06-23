package com.my.home.log;

import com.google.gson.Gson;
import com.my.home.base.DataToTest;
import com.my.home.base.TestBase;
import com.my.home.base.TestUtils;
import com.my.home.log.beans.LogNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * Test for log nodes parser
 */
public class LogNodeParserTest extends TestBase
{
    private static final String INPUT_SUB_DIR = "input/parser/";
    private LogNodeParser m_toTest;

    @Before
    public void setUp()
    {
        m_toTest = new LogNodeParser();
    }

    /**
     * Test of creating nodes from strings
     * @throws IOException Unexpected exception
     */
    @Test
    public void createNodesTest() throws IOException, ParseException
    {
        DataToTest settings = initParserWithFile("TestCase1");

        LogNode node = m_toTest.parse(settings.getStringsToTest().get(0));

        assertEquals("2016-08-26", node.getDate());
        assertEquals("13:53:11", node.getTime());
        assertEquals("248", node.getMillisecond());
        assertEquals("DEBUG", node.getLogLevel());
        assertEquals("WorkManager(2)-71", node.getThread());
        assertEquals("com.datalex.m3.servants.reservation.retrieve.bean.ReservationRetrieveSvBean", node.getClassPackage());
        assertEquals("logServantTimestamp: true", node.getMessage());

        node = m_toTest.parse(settings.getStringsToTest().get(1));

        assertEquals("2016-08-26", node.getDate());
        assertEquals("13:53:11", node.getTime());
        assertEquals("217", node.getMillisecond());
        assertEquals("DEBUG", node.getLogLevel());
        assertEquals("WorkManager(2)-43", node.getThread());
        assertEquals("com.datalex.matrix.servants.resolution.utils.AdaptorResolverUtils", node.getClassPackage());
        assertEquals("Number of validated point of sale override configurations being returned is 1", node.getMessage());
    }

    /**
     * Check if after parsing parser can create the same string
     * @throws IOException
     */
    @Test
    public void rollBackedLogTest() throws IOException, ParseException
    {
        DataToTest settings = initParserWithFile("TestCase1");
        for (String logLine : settings.getStringsToTest())
        {
            LogNode node = m_toTest.parse(logLine);
            String backedLine = m_toTest.parse(node);
            assertEquals(logLine, backedLine);
            assertNotEquals(System.identityHashCode(logLine), System.identityHashCode(backedLine));
        }
    }

    /******************************************/
    /**          HELPER METHODS              **/
    /******************************************/

    /**
     * Init parser with some file
     * @param fileName - file to setup
     * @throws IOException - unexpected exception
     */
    private DataToTest initParserWithFile(String fileName) throws IOException
    {
        String testCase1 = loadFile(INPUT_SUB_DIR + fileName);
        Gson gson = new Gson();
        DataToTest settings = (DataToTest) gson.fromJson(testCase1, DataToTest.class);
        TestUtils.setUpParser(m_toTest, settings);
        return settings;
    }

}
