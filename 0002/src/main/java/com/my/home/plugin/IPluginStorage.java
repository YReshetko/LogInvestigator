package com.my.home.plugin;

import com.my.home.plugin.model.PluginToStore;

import java.util.List;

/**
 *
 */
public interface IPluginStorage
{
    void save(PluginToStore plugin);
    List<PluginToStore> get();
    void update(PluginToStore plugin);
    void remove(PluginToStore plugin);
}
