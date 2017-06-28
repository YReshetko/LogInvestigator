package com.my.home.plugin;

import com.my.home.plugin.model.PluginToStore;

import java.io.File;
import java.util.List;

/**
 *
 */
public interface IPluginFactory
{
    List<PluginToStore> savePlugin(File file);
    void updatePlugins();
    <V> V getPlugin(PluginToStore plugin);

}
