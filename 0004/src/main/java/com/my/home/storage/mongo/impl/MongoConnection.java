package com.my.home.storage.mongo.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.my.home.storage.mongo.IMongoLogAccess;
import com.my.home.storage.mongo.IMongoPluginAccess;
import com.my.home.storage.mongo.plugin.MongoPluginAccess;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MongoConnection
{
    private static final String PLUGIN_COLLECTION = "plugins";
    private String host;
    private int port;
    private String dbName;

    private MongoClient client;
    private DB db;

    private Map<String, IMongoLogAccess> accesses;
    private IMongoPluginAccess pluginAccess;

    public void init()
    {
        client = new MongoClient(host, port);
        db = client.getDB(dbName);
        accesses = new HashMap<>();
        DBCollection mongoCollection = db.getCollection(PLUGIN_COLLECTION);
        pluginAccess = new MongoPluginAccess(mongoCollection);
    }

    public <V> IMongoLogAccess<V> getAccess(String collection, Class<V> type)
    {
        IMongoLogAccess access = accesses.get(collection);
        if (access == null)
        {
            DBCollection mongoCollection = db.getCollection(collection);
            access = new MongoLogAccess(mongoCollection, type);
            accesses.put(collection, access);
        }
        return access;
    }
    public IMongoPluginAccess getPluginAccess()
    {
        return pluginAccess;
    }
    public void closeConnection()
    {
        if(client != null)
        {
            client.close();
        }
    }

    public void reconnect()
    {
        closeConnection();
        init();
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
