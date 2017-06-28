package com.my.home.plugin;

import com.my.home.log.beans.LogNode;

import java.util.Map;

/**
 *
 */
public interface IPluginProcessor extends IPluginSetup
{
    /**
     * Process separate log node to collect some information
     * @param node - log node
     */
    void process(LogNode node);

    /**
     * @return - result of plugin execution
     */
    Map<String, Object> getResult();
}
