package com.my.home.plugin.processing;

import com.my.home.log.beans.LogNode;
import com.my.home.plugin.IPluginFilter;
import com.my.home.plugin.IPluginPostProcessor;
import com.my.home.plugin.IPluginProcessor;
import com.my.home.plugin.IPluginSelector;
import com.my.home.storage.ILogIdentifier;

import java.util.Iterator;
import java.util.List;

/**
 * Contains plugins and log iterator to process
 */
public class ProcessingConfigurationBean
{
    private List<IPluginSelector> selectors;
    private List<IPluginFilter> filters;
    private List<IPluginProcessor> processors;
    private List<IPluginPostProcessor> postProcessors;
    private Iterator<LogNode> log;
    private ILogIdentifier logIdentifier;

    public ProcessingConfigurationBean addSelectors(List<IPluginSelector> selectors)
    {
        this.selectors = selectors;
        return this;
    }
    public ProcessingConfigurationBean addFilters(List<IPluginFilter> filters)
    {
        this.filters = filters;
        return this;
    }
    public ProcessingConfigurationBean addProcessors(List<IPluginProcessor> processors)
    {
        this.processors = processors;
        return this;
    }
    public ProcessingConfigurationBean addPostProcessors(List<IPluginPostProcessor> postProcessors)
    {
        this.postProcessors = postProcessors;
        return this;
    }
    public ProcessingConfigurationBean addLog(Iterator<LogNode> log)
    {
        this.log = log;
        return this;
    }
    public ProcessingConfigurationBean addLogIdentifier(ILogIdentifier logIdentifier)
    {
        this.logIdentifier = logIdentifier;
        return this;
    }

    public List<IPluginSelector> getSelectors() {
        return selectors;
    }

    public List<IPluginFilter> getFilters() {
        return filters;
    }

    public List<IPluginProcessor> getProcessors() {
        return processors;
    }

    public List<IPluginPostProcessor> getPostProcessors() {
        return postProcessors;
    }

    public Iterator<LogNode> getLog() {
        return log;
    }

    public ILogIdentifier getLogIdentifier() {
        return logIdentifier;
    }
}
