package com.my.home.storage.mongo.plugin;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.storage.mongo.IMongoPluginAccess;
import com.my.home.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MongoPluginAccess implements IMongoPluginAccess
{
    private static final String PARAMS_TEMPLATE = "\"%s\" : \"%s\"";
    private static final String SELECTOR_TEMPLATE = "{ %s }";

    private DBCollection collection;
    public MongoPluginAccess(DBCollection collection)
    {
        this.collection = collection;
    }
    /**
     * Insert info about plugin into storage
     *
     * @param plugin - plugin to insert
     */
    @Override
    public void insert(PluginToStore plugin) {
        if (!contains(plugin))
        {
            DBObject toSave = (DBObject)JSON.parse(JsonUtils.getJson(plugin));
            collection.insert(toSave);
        }
        else
        {
            System.out.println("Such plugin already exists into storage");
            //throw new IllegalArgumentException("Such plugin already exists into storage");
        }
    }

    /**
     * Retrieve all plugins in storage
     *
     * @return - list of plugins description
     */
    @Override
    public List<PluginToStore> findAll()
    {
        DBCursor cursor = collection.find();
        List<PluginToStore> out = new ArrayList<>();
        while (cursor.hasNext())
        {
            out.add(JsonUtils.getObject(JSON.serialize(cursor.next()), PluginToStore.class));
        }
        return out;
    }

    /**
     * Check if storage already contain the plugin
     *
     * @param plugin - plugin
     * @return - true if contains
     */
    @Override
    public boolean contains(PluginToStore plugin)
    {
        long count = collection.count(preparePluginSelector(plugin));
        return count > 0;
    }

    /**
     * Find the plugin with the same id and update it
     *
     * @param plugin - plugin to update
     */
    @Override
    public void update(PluginToStore plugin)
    {
        DBObject select = preparePluginSelector(plugin);
        DBObject update = (DBObject)JSON.parse(JsonUtils.getJson(plugin));
        collection.update(select, update);
    }

    /**
     * Remove plugin from storage
     *
     * @param plugin - plugin to remove
     */
    @Override
    public void remove(PluginToStore plugin) {
        DBObject select = preparePluginSelector(plugin);
        collection.remove(select);
    }

    /**
     * Prepares selector to retrieve unique plugin by ID, label, package and class
     * @param plugin - plugin to search
     * @return - DBObject to search
     */
    private DBObject preparePluginSelector(PluginToStore plugin)
    {
        List<String> collect = new ArrayList<>();
        if(plugin.getId() != null && !plugin.getId().isEmpty())
        {
            collect.add(String.format(PARAMS_TEMPLATE, "id", plugin.getId()));
        }
        if(plugin.getPlugin().getLabel() != null && !plugin.getPlugin().getLabel().isEmpty())
        {
            collect.add(String.format(PARAMS_TEMPLATE, "plugin.label", plugin.getPlugin().getLabel()));
        }
        if(plugin.getPlugin().getClassName() != null && !plugin.getPlugin().getClassName().isEmpty())
        {
            collect.add(String.format(PARAMS_TEMPLATE, "plugin.className", plugin.getPlugin().getClassName()));
        }
        if(plugin.getPlugin().getPackageName() != null && !plugin.getPlugin().getPackageName().isEmpty())
        {
            collect.add(String.format(PARAMS_TEMPLATE, "plugin.packageName", plugin.getPlugin().getPackageName()));
        }
        String command = "";
        if(collect.size() > 0)
        {
            StringBuilder builder = new StringBuilder(collect.get(0));
            for (int i = 1; i < collect.size(); i++)
            {
                builder.append(", ").append(collect.get(i));
            }
            command = String.format(SELECTOR_TEMPLATE, builder.toString());
        }
        DBObject out = (DBObject)JSON.parse(command);
        return out;
    }
}
