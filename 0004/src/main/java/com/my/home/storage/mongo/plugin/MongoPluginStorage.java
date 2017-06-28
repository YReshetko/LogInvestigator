package com.my.home.storage.mongo.plugin;

import com.my.home.plugin.IPluginStorage;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.storage.mongo.IMongoPluginAccess;
import com.my.home.storage.mongo.impl.MongoConnection;

import java.util.List;

/**
 *
 */
public class MongoPluginStorage implements IPluginStorage
{
    MongoConnection connection;
    public MongoPluginStorage(MongoConnection connection)
    {
        this.connection = connection;
    }
    @Override
    public void save(PluginToStore pluginToStore) {
        IMongoPluginAccess access = connection.getPluginAccess();
        access.insert(pluginToStore);
    }

    @Override
    public List<PluginToStore> get()
    {
        IMongoPluginAccess access = connection.getPluginAccess();
        return access.findAll();
    }

    @Override
    public void update(PluginToStore pluginToStore) {
        IMongoPluginAccess access = connection.getPluginAccess();
        access.update(pluginToStore);
    }

    @Override
    public void remove(PluginToStore pluginToStore) {
        IMongoPluginAccess access = connection.getPluginAccess();
        access.remove(pluginToStore);
    }
}
