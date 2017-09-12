package com.my.home.plugin.processing;

import com.my.home.log.beans.LogNode;
import com.my.home.plugin.IPluginFilter;
import com.my.home.plugin.IPluginPostProcessor;
import com.my.home.plugin.IPluginProcessor;
import com.my.home.plugin.IPluginSelector;
import com.my.home.plugin.model.PluginOutput;
import com.my.home.storage.ILogStorageContext;

import java.util.ArrayList;
import java.util.Iterator;
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

        List<IPluginSelector> selectors = config.getSelectors();
        /**
         * TODO Uncomment when it will be useful
         */
        /*final List<ILogStorageCommand<LogNode>> selectorCommands = new ArrayList<>();
        selectors.forEach(selector -> selectorCommands.add(selector.getRequest()));
        context.getRetriever().get(config.getLogIdentifier(), ???);*/

        List<IPluginFilter> filters = config.getFilters();
        final List<IPluginProcessor> processors = config.getProcessors();
        List<IPluginPostProcessor> postProcessors = config.getPostProcessors();
        Iterator<LogNode> nodes = config.getLog();
        LogNode node;
        boolean doNotFilter;
        boolean needsFiltering = filters != null && !filters.isEmpty();
        boolean needsProcessor = processors != null && !processors.isEmpty();
        while (nodes.hasNext())
        {
            node = nodes.next();
            doNotFilter = true;
            if(needsFiltering)
            {
                for (IPluginFilter filter : filters)
                {
                    doNotFilter &= !filter.filter(node);
                }
            }
            if(doNotFilter && needsProcessor)
            {
                for (IPluginProcessor processor : processors)
                {
                    processor.process(node);
                }

            }
        }
        final List<PluginOutput> output = new ArrayList<>();
        if(needsProcessor)
        {
            processors.get(0).getResult();
            processors.forEach(processor -> output.add(processor.getResult()));
        }
        return output;
    }
}
