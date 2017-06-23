package com.my.home.base;

import com.my.home.log.LogNodeParser;

/**
 * Utils for tests
 */
public class TestUtils
{
    public static void setUpParser(LogNodeParser parser, DataToTest settings)
    {
        parser.setCommonStampPattern(settings.getCommonStampPattern());
        parser.setCommonDataTimePattern(settings.getCommonDataTimePattern());
        parser.setDatePattern(settings.getDatePattern());
        parser.setTimePattern(settings.getTimePattern());
        parser.setMillisecondsPattern(settings.getMillisecondsPattern());
        parser.setLogLevelPattern(settings.getLogLevelPattern());
        parser.setThreadPatten(settings.getThreadPatten());
        parser.setClassPattern(settings.getClassPattern());
        parser.setDateFormat(settings.getDateFormat());
        parser.setTimeFormat(settings.getTimeFormat());
        parser.init();
    }
}
