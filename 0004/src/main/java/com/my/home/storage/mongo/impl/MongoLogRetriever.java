package com.my.home.storage.mongo.impl;

import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.processor.ILogStorageCommand;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogRetriever;
import com.my.home.storage.mongo.IMongoLogAccess;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class MongoLogRetriever extends MongoLogBase implements ILogRetriever
{
    public MongoLogRetriever(MongoConnection connection) {
        super(connection);
    }

    @Override
    public <V> Iterator<V> get(ILogIdentifier identifier, ILogStorageCommand<V> iLogStorageCommand)
    {
        Class<V> vClass = iLogStorageCommand.getType();
        IMongoLogAccess<V> access = getAccess(identifier, getCollection(vClass));
        Iterator<V> out = null;
        access.setSortBy(iLogStorageCommand.sortBy());
        if(iLogStorageCommand.getCommandType() == ILogStorageCommand.Command.FIND_ALL)
        {
            out = access.findAll();
        }
        else
        {
            List<String> commands = iLogStorageCommand.getSelectors();
            if(commands == null)
            {
                String command = iLogStorageCommand.getSelector();
                out = access.find(command);
            }
            else
            {
                out = access.find(commands);
            }
        }
        return out;
    }

    @Override
    public <V> void changeLog(ILogIdentifier identifier, ILogStorageCommand<V> iLogStorageCommand) {
        Class<V> vClass = iLogStorageCommand.getType();
        IMongoLogAccess<V> access = getAccess(identifier, getCollection(vClass));
        ILogStorageCommand.Command command = iLogStorageCommand.getCommandType();
        if(command == ILogStorageCommand.Command.UPDATE)
        {
            access.update(iLogStorageCommand.getOldValue(), iLogStorageCommand.getNewValue());
        }
        else if (command == ILogStorageCommand.Command.REMOVE)
        {
            access.remove(iLogStorageCommand.getSelector());
        }
        else if(command == ILogStorageCommand.Command.REMOVE_ALL)
        {
            access.removeAll();
        }
        else
        {
            throw new IllegalArgumentException("Wrong type of command to make change into storage: " + command);
        }

    }
}
