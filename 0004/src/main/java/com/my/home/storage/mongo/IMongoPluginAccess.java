package com.my.home.storage.mongo;

import com.my.home.plugin.model.PluginToStore;

import java.util.List;

/**
 *
 */
public interface IMongoPluginAccess
{
    /**
     * Insert info about plugin into storage
     * @param plugin - plugin to insert
     */
    void insert(PluginToStore plugin);

    /**
     * Retrieve all plugins in storage
     * @return - list of plugins description
     */
    List<PluginToStore> findAll();

    /**
     * Check if storage already contain the plugin
     * @param plugin - plugin
     * @return - true if contains
     */
    boolean contains(PluginToStore plugin);

    /**
     * Find the plugin with the same id and update it
     * @param plugin - plugin to update
     */
    void update(PluginToStore plugin);

    /**
     * Remove plugin from storage
     * @param plugin - plugin to remove
     */
    void remove(PluginToStore plugin);

}
