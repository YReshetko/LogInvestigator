package com.my.home.parser;

import com.my.home.log.LogNodeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ParserManager
{
    private static final String DEFAULT_PARSER_NAME = "default";
    private LogNodeParser defaultParser;

    private Map<String, LogNodeParser> parsers;
    public ParserManager(LogNodeParser defaultParser)
    {
        this.defaultParser = defaultParser;
        parsers = new HashMap<>();
    }
    public LogNodeParser getParser(String name)
    {
        LogNodeParser out = parsers.get(name);
        if(out == null)
        {
            out = defaultParser;
        }
        return out;
    }
    public List<String> getAllParsers()
    {
        List<String> out = new ArrayList<>();
        out.add(DEFAULT_PARSER_NAME);
        parsers.entrySet().forEach(entry -> out.add(entry.getKey()));
        return out;
    }
    public void saveParser(String name, LogNodeParser parser)
    {
        // TODO implement saving parsers
        throw new UnsupportedOperationException("Project has not support this operation, yet");
    }
}
