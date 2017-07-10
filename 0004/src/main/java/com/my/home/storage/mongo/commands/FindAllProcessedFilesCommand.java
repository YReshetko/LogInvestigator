package com.my.home.storage.mongo.commands;

import com.my.home.log.beans.LogFilesDescriptor;

/**
 *
 */
public class FindAllProcessedFilesCommand extends AbstractStorageCommand<LogFilesDescriptor>
{
    @Override
    public String getSelector() {
        return null;
    }

    @Override
    public Class<LogFilesDescriptor> getType() {
        return LogFilesDescriptor.class;
    }

    @Override
    public Command getCommandType() {
        return Command.FIND_ALL;
    }
}
