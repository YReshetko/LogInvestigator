package com.my.home.storage.mongo.impl;

import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.mongo.IMongoLogAccess;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MongoLogBase
{
    private MongoConnection connection;

    enum LogCollection
    {
        NODES_COLLECTION("node", LogNode.class),
        THREADS_COLLECTION("threads", ThreadDescriptor.class),
        INFO_COLLECTION("info", ThreadsInfo.class),
        LOG_DESCRIPTORS("logDescriptors", LogFilesDescriptor.class, false);

        private static final String COLLECTION_NAME_FORMAT = "%s_%s";
        private String tableName;
        private Class aClass;
        private boolean usePrefix;
        LogCollection(String tableName, Class aClass)
        {
            this(tableName, aClass, true);
        }
        LogCollection(String tableName, Class aClass, boolean usePrefix)
        {
            this.tableName = tableName;
            this.aClass = aClass;
            this.usePrefix = usePrefix;
        }
        private String getCollectionName(ILogIdentifier identifier)
        {
            if(usePrefix)
            {
                return String.format(COLLECTION_NAME_FORMAT, tableName, identifier.getKey());
            }
            else
            {
                return tableName;
            }

        }
    }

    private Map<Class, LogCollection> collectionMap;
    public MongoLogBase(MongoConnection connection) {
        this.connection = connection;
        collectionMap = new HashMap<>();
        for(LogCollection logCollection : LogCollection.values())
        {
            collectionMap.put(logCollection.aClass, logCollection);
        }
    }
    protected IMongoLogAccess getAccess(ILogIdentifier identifier, LogCollection collection)
    {
        return connection.getAccess(collection.getCollectionName(identifier), collection.aClass);
    }
    protected <V> LogCollection getCollection(Class<V> vClass)
    {
        return collectionMap.get(vClass);
    }
}
