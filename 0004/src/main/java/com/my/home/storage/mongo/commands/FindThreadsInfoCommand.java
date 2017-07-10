package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.ThreadsInfo;

/**
 *
 */
public class FindThreadsInfoCommand extends AbstractStorageCommand<ThreadsInfo> {
    @Override
    public Class<ThreadsInfo> getType() {
        return ThreadsInfo.class;
    }

    @Override
    public Command getCommandType() {
        return Command.FIND;
    }

    @Override
    public String getSelector() {
        return null;
    }
}
