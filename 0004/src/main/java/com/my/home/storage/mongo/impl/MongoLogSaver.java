package com.my.home.storage.mongo.impl;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.mongo.IMongoAccess;
import com.my.home.storage.mongo.wrapper.WrapperToSave;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 25.06.17
 * Time: 6:41
 * To change this template use File | Settings | File Templates.
 */
public class MongoLogSaver implements ILogSaver
{
    private static final String NODES_COLLECTION = "nodes";
    private static final String THREADS_COLLECTION = "threads";
    private static final String INFO_COLLECTION = "info";
    private MongoConnection connection;
    public MongoLogSaver(MongoConnection connection)
    {
        this.connection = connection;
    }
    @Override
    public boolean saveNode(ILogIdentifier identifier, LogNode node)
    {
        return saveObject(NODES_COLLECTION, identifier, node);
    }

    @Override
    public boolean saveThreadsInfo(ILogIdentifier identifier, ThreadsInfo threadsInfo) {
        return saveObject(INFO_COLLECTION, identifier, threadsInfo);
    }

    @Override
    public boolean saveThreadDescriptor(ILogIdentifier identifier, ThreadDescriptor descriptor) {
        return saveObject(THREADS_COLLECTION, identifier, descriptor);
    }

    private <V> boolean saveObject(String collection, ILogIdentifier identifier, V value)
    {
        IMongoAccess access = connection.getAccess(collection);
        WrapperToSave<V> toSave = new WrapperToSave<V>(identifier, value);
        access.insert(toSave.getRecordToSave());
        return true;
    }

    @Override
    public boolean complete(ILogIdentifier identifier, List<File> files) {
        return false;
    }
}
