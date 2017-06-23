package com.my.home.log;

import com.google.gson.Gson;
import com.my.home.base.TestBase;
import com.my.home.log.beans.LogNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

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
        assertEquals("277", node.getMillisecond());
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
        setUpParser(settings);
        return settings;
    }

    /**
     * Set up m_toTest to test
     * @param settings - settings for m_toTest
     */
    private void setUpParser(DataToTest settings)
    {
        m_toTest.setCommonStampPattern(settings.getCommonStampPattern());
        m_toTest.setCommonDataTimePattern(settings.getCommonDataTimePattern());
        m_toTest.setDatePattern(settings.getDatePattern());
        m_toTest.setTimePattern(settings.getTimePattern());
        m_toTest.setMillisecondsPattern(settings.getMillisecondsPattern());
        m_toTest.setLogLevelPattern(settings.getLogLevelPattern());
        m_toTest.setThreadPatten(settings.getThreadPatten());
        m_toTest.setClassPattern(settings.getClassPattern());
        m_toTest.setDateFormat(settings.getDateFormat());
        m_toTest.setTimeFormat(settings.getTimeFormat());
        m_toTest.init();
    }

    /**
     * Class for settings to set up parser
     */
    private class DataToTest
    {
        private String commonStampPattern;
        private String commonDataTimePattern;
        private String datePattern;
        private String timePattern;
        private String millisecondsPattern;
        private String logLevelPattern;
        private String threadPatten;
        private String classPattern;
        private String dateFormat;
        private String timeFormat;
        private ArrayList<String> stringsToTest;

        private String getCommonStampPattern() {
            return commonStampPattern;
        }

        private void setCommonStampPattern(String commonStampPattern) {
            this.commonStampPattern = commonStampPattern;
        }

        private String getCommonDataTimePattern() {
            return commonDataTimePattern;
        }

        private void setCommonDataTimePattern(String commonDataTimePattern) {
            this.commonDataTimePattern = commonDataTimePattern;
        }

        private String getDatePattern() {
            return datePattern;
        }

        private void setDatePattern(String datePattern) {
            this.datePattern = datePattern;
        }

        private String getTimePattern() {
            return timePattern;
        }

        private void setTimePattern(String timePattern) {
            this.timePattern = timePattern;
        }

        private String getMillisecondsPattern() {
            return millisecondsPattern;
        }

        private void setMillisecondsPattern(String millisecondsPattern) {
            this.millisecondsPattern = millisecondsPattern;
        }

        private String getLogLevelPattern() {
            return logLevelPattern;
        }

        private void setLogLevelPattern(String logLevelPattern) {
            this.logLevelPattern = logLevelPattern;
        }

        private String getThreadPatten() {
            return threadPatten;
        }

        private void setThreadPatten(String threadPatten) {
            this.threadPatten = threadPatten;
        }

        private String getClassPattern() {
            return classPattern;
        }

        private void setClassPattern(String classPattern) {
            this.classPattern = classPattern;
        }

        private String getDateFormat() {
            return dateFormat;
        }

        private void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        private String getTimeFormat() {
            return timeFormat;
        }

        private void setTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
        }

        private ArrayList<String> getStringsToTest() {
            return stringsToTest;
        }

        private void setStringsToTest(ArrayList<String> stringsToTest) {
            this.stringsToTest = stringsToTest;
        }
    }
}
