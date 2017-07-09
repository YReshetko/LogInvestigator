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
        Class<V> vClass = (Class<V>) ((ParameterizedType) iLogStorageCommand.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        IMongoLogAccess<V> access = getAccess(identifier, getCollection(vClass));
        String command = iLogStorageCommand.getCommand();
        Iterator<V> out = null;
        access.setSortBy(iLogStorageCommand.sortBy());
        if(command == null)
        {
            out = access.findAll();
        }
        else
        {
            out = access.find(command);
        }
        return out;
    }

    @Override
    public void changeLog(ILogIdentifier identifier, ILogStorageCommand iLogStorageCommand) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
