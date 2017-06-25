package com.my.home.storage.mongo.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.my.home.storage.mongo.IMongoAccess;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MongoConnection
{
    private String host;
    private int port;
    private String dbName;

    private MongoClient client;
    private DB db;

    private Map<String, MongoAccess> accesses;

    public void init()
    {
        client = new MongoClient(host, port);
        db = client.getDB(dbName);
        accesses = new HashMap<>();
    }

    public IMongoAccess getAccess(String collection)
    {
        MongoAccess access = accesses.get(collection);
        if (access == null)
        {
            DBCollection mongoCollection = db.getCollection(collection);
            access = new MongoAccess(mongoCollection);
            accesses.put(collection, access);
        }
        return access;
    }
    public void closeConnection()
    {
        if(client != null)
        {
            client.close();
        }
    }

    @Override
    public void finalize()
    {
        closeConnection();
    }

}
