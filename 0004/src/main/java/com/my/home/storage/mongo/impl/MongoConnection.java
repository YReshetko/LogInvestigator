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

    public <V> IMongoAccess<V> getAccess(String collection, Class<V> type)
    {
        MongoAccess access = accesses.get(collection);
        if (access == null)
        {
            DBCollection mongoCollection = db.getCollection(collection);
            access = new MongoAccess(mongoCollection, type);
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

}
