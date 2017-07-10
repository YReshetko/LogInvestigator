package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.ThreadsInfo;
import com.my.home.util.JsonUtils;

import java.util.List;

/**
 *
 */
public class RemoveThreadsCommand extends  AbstractStorageCommand<ThreadsInfo>
{
    private String oldThreadsInfo;
    private String newThreadsInfo;

    public RemoveThreadsCommand(ThreadsInfo oldValue, List<String> threadsToRemove)
    {
        oldThreadsInfo = JsonUtils.getJson(oldValue);
        oldValue.getThreads().
                stream().
                filter(entry -> threadsToRemove.contains(entry.getThreadName())).
                forEach(entry -> entry.setIsDelete(true));
        newThreadsInfo = JsonUtils.getJson(oldValue);
    }
    @Override
    public Class<ThreadsInfo> getType() {
        return ThreadsInfo.class;
    }

    @Override
    public Command getCommandType() {
        return Command.UPDATE;
    }

    @Override
    public String getOldValue() {
        return oldThreadsInfo;
    }

    @Override
    public String getNewValue() {
        return newThreadsInfo;
    }
}
