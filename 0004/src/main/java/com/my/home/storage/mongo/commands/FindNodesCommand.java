package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FindNodesCommand extends AbstractStorageCommand<LogNode>
{
    private static final String ID_TEMP = "\"id\":\"%s\"";
    private static final String THREAD_TEMP = "\"thread\":\"%s\"";
    private static final String DATE_TEMP = "\"date\":\"%s\"";
    private static final String TIME_TEMP = "\"time\":\"%s\"";
    private static final String MILLISECONDS_TEMP = "\"millisecond\":\"%s\"";
    private static final String LOG_LEVEL_TEMP = "\"logLevel\":\"%s\"";
    private static final String CLASS_PACKAGE_TEMP = "\"classPackage\":\"%s\"";
    private static final String MESSAGE_TEMP = "\"message\":\"%s\"";
    private static final String LONG_DATE_TIME_TEMP = "\"longDateTime\":%s";

    @Override
    public String getCommand()
    {
        List<String> cases = getListCommand(value);
        StringBuilder builder = new StringBuilder();
        builder.append("{$or : [");
        builder.append(split(cases));
        builder.append("]}");
        return builder.toString();
    }



    protected List<String> getListCommand(List<LogNode> value)
    {
        List<String> out = new ArrayList<>();
        List<String> buff;
        for (LogNode node : value)
        {
            buff = new ArrayList<>();
            if(node.getId() != null)
            {
                buff.add(String.format(ID_TEMP, node.getId()));
            }
            if(node.getThread() != null)
            {
                buff.add(String.format(THREAD_TEMP, node.getThread()));
            }
            if(node.getDate() != null)
            {
                buff.add(String.format(DATE_TEMP, node.getDate()));
            }
            if(node.getTime() != null)
            {
                buff.add(String.format(TIME_TEMP, node.getTime()));
            }
            if(node.getMillisecond() != null)
            {
                buff.add(String.format(MILLISECONDS_TEMP, node.getMillisecond()));
            }
            if(node.getLogLevel() != null)
            {
                buff.add(String.format(LOG_LEVEL_TEMP, node.getLogLevel()));
            }
            if(node.getClassPackage() != null)
            {
                buff.add(String.format(CLASS_PACKAGE_TEMP, node.getClassPackage()));
            }
            if(node.getMessage() != null)
            {
                buff.add(String.format(MESSAGE_TEMP, node.getMessage()));
            }
            if(node.getLongDateTime() != null)
            {
                buff.add(String.format(LONG_DATE_TIME_TEMP, node.getLongDateTime()));
            }
            if(buff.size() > 0)
            {
                out.add("{"+split(buff)+"}");
            }
        }
        return out;
    }

    @Override
    public String sortBy() {
        return "id";
    }
}
