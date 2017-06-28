package com.my.home.plugin;

import java.util.Map;

/**
 *
 */
public interface IPluginSetup
{
    /**
     * Common method for all plugins to setup initial parameters of execution
     * @param properties - input properties
     */
    void setProperties(Map<String, Object> properties);

    /**
     * Returns true if user have to setup initial properties
     * @return - - bool
     */
    boolean setupRequires();
}
