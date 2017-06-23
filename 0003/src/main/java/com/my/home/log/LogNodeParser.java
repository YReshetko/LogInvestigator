package com.my.home.log;

import com.my.home.ILogNodeParser;
import com.my.home.log.beans.LogNode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   Parser of log nodes from/to string
 */
public class LogNodeParser implements ILogNodeParser
{
    /**
     * D - pattern for date
     * T - pattern time
     * M - pattern milliseconds
     * L - pattern log level
     * S - pattern thread (Stream)
     * C - pattern class
     * M - pattern log
     */
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

    private String stampPattern;
    private String dataTimePattern;
    private Map<CommonStamps, String> patternMap;
    private List<Character> revertPatternMap;

    /**
     * Initialization method
     * Make template full stamp
     */
    public void init()
    {
        initPatternMap();
        stampPattern = buildCommonPattern(commonStampPattern);
        dataTimePattern = buildCommonPattern(commonDataTimePattern);
    }

    /**
     *  Create pattern map where mapped patterns on groups and letters
     */
    private void initPatternMap()
    {
        patternMap = new EnumMap<CommonStamps, String>(CommonStamps.class);
        patternMap.put(CommonStamps.DATE_PATTERN, datePattern);
        patternMap.put(CommonStamps.TIME_PATTERN, timePattern);
        patternMap.put(CommonStamps.MILLISECONDS_PATTERN, millisecondsPattern);
        patternMap.put(CommonStamps.LOG_LEVEL_PATTERN, logLevelPattern);
        patternMap.put(CommonStamps.THREAD_PATTERN, threadPatten);
        patternMap.put(CommonStamps.CLASS_PATTERN, classPattern);
    }

    /**
     * Build pattern for full stamp
     * @param commonPattern
     * @return
     */
    private String buildCommonPattern(String commonPattern)
    {
        StringBuilder out = new StringBuilder();
        CommonStamps[] commonStamps = CommonStamps.values();
        boolean isPattern;
        int i,l,j,n;
        int groupNumber = 0;
        l = commonPattern.length();
        n = commonStamps.length;

        for (i = 0; i < l; i++)
        {
            isPattern = false;
            for (j = 0; j < n; j++)
            {
                if (commonPattern.charAt(i) == commonStamps[j].getSymbol())
                {
                    ++groupNumber;
                    out.append("(");
                    out.append(patternMap.get(commonStamps[j]));
                    out.append(")");
                    isPattern = true;
                    commonStamps[j].setGroupNumber(groupNumber);
                    break;
                }
            }
            if(!isPattern)
            {
                out.append(commonPattern.charAt(i));
            }
        }
        return out.toString();
    }
    @Override
    public LogNode parse(String value) throws ParseException
    {
        LogNode out = new LogNode();
        Pattern reg = Pattern.compile(stampPattern);
        Matcher matcher = reg.matcher(value);
        if (matcher.find())
        {
            out.setDate(matcher.group(CommonStamps.DATE_PATTERN.getGroupNumber()));
            out.setTime(matcher.group(CommonStamps.TIME_PATTERN.getGroupNumber()));
            out.setMillisecond(matcher.group(CommonStamps.MILLISECONDS_PATTERN.getGroupNumber()));
            out.setLogLevel(matcher.group(CommonStamps.LOG_LEVEL_PATTERN.getGroupNumber()));
            out.setThread(matcher.group(CommonStamps.THREAD_PATTERN.getGroupNumber()));
            out.setClassPackage(matcher.group(CommonStamps.CLASS_PATTERN.getGroupNumber()));
            out.setMessage(value.replaceAll(stampPattern, ""));
            out.setLongDateTime(getMillisecond(out.getDate(), out.getTime(), out.getMillisecond()));
            out.setIsStamped(true);
        }
        else
        {
            out.setMessage(value);
        }
        return out;
    }
    @Override
    public String parse(LogNode node)
    {
        if (node.isIsStamped())
        {
            StringBuilder out = new StringBuilder();
            int i,l;
            l = commonStampPattern.length();
            for (i=0;i<l;i++){
                char ch = commonStampPattern.charAt(i);
                String letter = String.valueOf(ch);

                if (CommonStamps.STRING_PATTERNS.contains(letter))
                {
                    if (ch == CommonStamps.DATE_PATTERN.getSymbol())
                    {
                        out.append(node.getDate());
                    }
                    else if (ch == CommonStamps.TIME_PATTERN.getSymbol())
                    {
                        out.append(node.getTime());
                    }
                    else if (ch == CommonStamps.MILLISECONDS_PATTERN.getSymbol())
                    {
                        out.append(node.getMillisecond());
                    }
                    else if (ch == CommonStamps.LOG_LEVEL_PATTERN.getSymbol())
                    {
                        out.append(node.getLogLevel());
                    }
                    else if (ch == CommonStamps.THREAD_PATTERN.getSymbol())
                    {
                        out.append(node.getThread());
                    }
                    else if (ch == CommonStamps.CLASS_PATTERN.getSymbol())
                    {
                        out.append(node.getClassPackage());
                    }
                }
                else
                {
                    if (!"\\".equals(letter))
                    {
                        out.append(letter);
                    }
                }
            }
            out.append(node.getMessage());
            return out.toString();
        }
        else
        {
            return node.getMessage();
        }
    }
    @Override
    public boolean hasStamp(String value)
    {
        Pattern reg = Pattern.compile(stampPattern);
        Matcher matcher = reg.matcher(value);
        return matcher.find();
    }
    public long getMillisecond(String sDate, String sTime, String milliseconds) throws ParseException
    {
        DateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
        DateFormat timeFormat = new SimpleDateFormat(this.timeFormat);
        Date date = dateFormat.parse(sDate);
        Date time = timeFormat.parse(sTime);
        long millis = Long.parseLong(milliseconds);
        long out = millis + date.getTime() + time.getTime();
        return out;
    }


    public String getCommonStampPattern() {
        return commonStampPattern;
    }

    public void setCommonStampPattern(String commonStampPattern) {
        this.commonStampPattern = commonStampPattern;
    }

    public String getCommonDataTimePattern() {
        return commonDataTimePattern;
    }

    public void setCommonDataTimePattern(String commonDataTimePattern) {
        this.commonDataTimePattern = commonDataTimePattern;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public String getMillisecondsPattern() {
        return millisecondsPattern;
    }

    public void setMillisecondsPattern(String millisecondsPattern) {
        this.millisecondsPattern = millisecondsPattern;
    }

    public String getLogLevelPattern() {
        return logLevelPattern;
    }

    public void setLogLevelPattern(String logLevelPattern) {
        this.logLevelPattern = logLevelPattern;
    }

    public String getThreadPatten() {
        return threadPatten;
    }

    public void setThreadPatten(String threadPatten) {
        this.threadPatten = threadPatten;
    }

    public String getClassPattern() {
        return classPattern;
    }

    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    private enum CommonStamps
    {
        DATE_PATTERN('D'),
        TIME_PATTERN('T'),
        MILLISECONDS_PATTERN('M'),
        LOG_LEVEL_PATTERN('L'),
        THREAD_PATTERN('S'),
        CLASS_PATTERN('C');
        public static final String STRING_PATTERNS = "DTMLSC";
        private char pattern;
        private int groupNumber;
        private CommonStamps(char value)
        {
            pattern = value;
        }
        public char getSymbol()
        {
            return pattern;
        }

        private int getGroupNumber() {
            return groupNumber;
        }

        private void setGroupNumber(int groupNumber) {
            this.groupNumber = groupNumber;
        }
    }
}
