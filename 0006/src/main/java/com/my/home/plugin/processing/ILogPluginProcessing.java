package com.my.home.plugin.processing;

import com.my.home.plugin.model.PluginOutput;
import com.my.home.storage.ILogStorageContext;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The interface for log processing through plugins
 */
public interface ILogPluginProcessing
{
    Future<List<PluginOutput>> process(ProcessingConfigurationBean config);
    void setStorageContext(ILogStorageContext iLogStorageContext);
}
