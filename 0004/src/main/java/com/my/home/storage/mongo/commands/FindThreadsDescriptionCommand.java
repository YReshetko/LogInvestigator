package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.processor.ILogStorageCommand;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FindThreadsDescriptionCommand extends AbstractStorageCommand<ThreadDescriptor>
{
    private static final String ID_TEMP = "\"id\":\"%s\"";
    private static final String NAME_TEMP = "\"name\":\"%s\"";
    private static final String START_TIME_TEMP = "\"startTime\":\"%s\"";
    private static final String END_TIME_TEMP = "\"endTime\":\"%s\"";
    public FindThreadsDescriptionCommand(List<ThreadDescriptor> val)
    {
        this.value = val;
    }
    @Override
    public Class<ThreadDescriptor> getType() {
        return ThreadDescriptor.class;
    }

    @Override
    public Command getCommandType() {
        return Command.FIND;
    }

    @Override
    public String getSelector() {
        List<String> cases = getListCommand(value);
        StringBuilder builder = new StringBuilder();
        builder.append("{$or : [");
        builder.append(split(cases));
        builder.append("]}");
        return builder.toString();
    }

    protected List<String> getListCommand(List<ThreadDescriptor> value)
    {
        List<String> out = new ArrayList<>();
        List<String> buff;
        for (ThreadDescriptor thread : value)
        {
            buff = new ArrayList<>();
            if(thread.getId() != null)
            {
                buff.add(String.format(ID_TEMP, thread.getId()));
            }
            if(thread.getName() != null)
            {
                buff.add(String.format(NAME_TEMP, thread.getName()));
            }
            if(thread.getStartTime() != null)
            {
                buff.add(String.format(START_TIME_TEMP, thread.getStartTime()));
            }
            if(thread.getEndTime() != null)
            {
                buff.add(String.format(END_TIME_TEMP, thread.getEndTime()));
            }
            if(buff.size() > 0)
            {
                out.add("{"+split(buff)+"}");
            }
        }
        return out;
    }
}
