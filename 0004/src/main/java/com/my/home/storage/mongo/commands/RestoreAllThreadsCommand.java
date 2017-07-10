package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.ThreadsInfo;
import com.my.home.util.JsonUtils;

/**
 *
 */
public class RestoreAllThreadsCommand extends AbstractStorageCommand<ThreadsInfo>
{
    private String oldThreadsInfo;
    private String newThreadsInfo;

    public RestoreAllThreadsCommand(ThreadsInfo oldValue)
    {
        oldThreadsInfo = JsonUtils.getJson(oldValue);
        oldValue.getThreads().
                forEach(entry -> entry.setIsDelete(false));
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
