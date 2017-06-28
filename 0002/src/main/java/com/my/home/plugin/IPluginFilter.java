package com.my.home.plugin;

import com.my.home.log.beans.LogNode;

/**
 *
 */
public interface IPluginFilter extends IPluginSetup
{
    /**
     * Returns true if we can proceed with this log node
     * @param node - log node to check
     * @return - bool
     */
    boolean filter(LogNode node);
}
