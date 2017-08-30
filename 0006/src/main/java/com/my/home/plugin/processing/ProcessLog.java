package com.my.home.plugin.processing;

import com.my.home.plugin.model.PluginOutput;
import com.my.home.storage.ILogStorageContext;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Yury_Rashetska on 8/30/2017.
 */
public class ProcessLog implements Callable<List<PluginOutput>>
{
    private ProcessingConfigurationBean config;
    private ILogStorageContext context;
    public ProcessLog(ProcessingConfigurationBean config, ILogStorageContext iLogStorageContext)
    {
        this.config = config;
        this.context = iLogStorageContext;
    }
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public List<PluginOutput> call() throws Exception
    {

        return null;
    }
}
