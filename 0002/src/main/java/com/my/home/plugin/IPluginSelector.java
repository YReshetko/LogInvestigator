package com.my.home.plugin;

import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogStorageCommand;

/**
 *
 */
public interface IPluginSelector extends IPluginSetup
{
    /**
     * Prepare and return command to select LogNodes From Storage
     * @return - command to execute on storage side
     */
    ILogStorageCommand<LogNode> getRequest();

}
