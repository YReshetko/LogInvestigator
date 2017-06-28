package com.my.home.storage.mongo.impl;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.mongo.IMongoLogAccess;
import com.my.home.util.JsonUtils;

import java.io.File;
import java.util.List;

/**
 * Implementation of log saver on mongo DB
 */
public class MongoLogSaver extends MongoLogBase implements ILogSaver
{
    public MongoLogSaver(MongoConnection connection)
    {
        super(connection);
    }
    @Override
    public boolean saveNode(ILogIdentifier identifier, LogNode node)
    {
        return save(identifier, node);
    }

    @Override
    public boolean saveThreadsInfo(ILogIdentifier identifier, ThreadsInfo threadsInfo) {
        return save(identifier, threadsInfo);
    }

    @Override
    public boolean saveThreadDescriptor(ILogIdentifier identifier, ThreadDescriptor descriptor) {
        return save(identifier, descriptor);
    }

    private <V> boolean save(ILogIdentifier identifier, V value) {
        IMongoLogAccess access = getAccess(identifier, getCollection(value.getClass()));
        access.insert(JsonUtils.getJson(value));
        return true;
    }


    @Override
    public boolean complete(ILogIdentifier identifier, List<File> files) {
        return false;
    }
}
