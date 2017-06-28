package com.my.home.plugin;

import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogStorageCommand;

/**
 *
 */
public interface IPluginPostProcessor extends IPluginSetup
{
    /**
     * Process separate log node to collect some information
     * @param node - log node
     */
    void process(LogNode node);
    /**
     * Prepares and returns command to execute on log storage after processing
     * @return - command to execute on storage side
     */
    ILogStorageCommand<LogNode> getPostRequest();
}
