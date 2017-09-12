package com.my.home.plugin.processing;

import com.my.home.plugin.model.PluginOutput;
import com.my.home.storage.ILogStorageContext;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Implementation of processing log through plugin
 */
public class LogPluginProcessingImpl implements ILogPluginProcessing
{
    private ILogStorageContext storageContext;
    @Override
    public Future<List<PluginOutput>> process(ProcessingConfigurationBean config)
    {
        Callable<List<PluginOutput>> processLog = new ProcessLog(config, storageContext);
        FutureTask<List<PluginOutput>> future = new FutureTask<List<PluginOutput>>(processLog);
        new Thread(future).start();
        return future;
    }

    @Override
    public void setStorageContext(ILogStorageContext iLogStorageContext)
    {
        storageContext = iLogStorageContext;
    }

}
