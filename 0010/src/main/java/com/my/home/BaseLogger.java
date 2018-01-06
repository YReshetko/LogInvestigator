package com.my.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
public class BaseLogger
{
    private Logger logger;
    protected boolean isLogging()
    {
        return logger.isDebugEnabled();
    }

    protected void log(String msg)
    {
        getLogger().debug(msg);
    }
    protected void error(String msg, Throwable e)
    {
        getLogger().error(msg, e);
    }

    private Logger getLogger()
    {
        if(logger == null)
        {
            logger = LogManager.getLogger(this.getClass().getName());
        }
        return logger;
    }
}
