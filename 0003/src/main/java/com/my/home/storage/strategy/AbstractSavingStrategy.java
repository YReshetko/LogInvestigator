package com.my.home.storage.strategy;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.LogMetaInfCache;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 13.07.17
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSavingStrategy implements ILogSavingStrategy
{
    private final LogMetaInfCache cache;
    public AbstractSavingStrategy()
    {
        cache = new LogMetaInfCache();
    }
    protected void cachingMetaInf(LogNode node)
    {
        cache.cacheMetaInf(node);
    }
    /**
     * Save meta information about log (threads info)
     * @param identifier - log identifier
     * @return - true if saving has success
     */
    protected boolean saveMetaInf(ILogIdentifier identifier, ILogSaver saver)
    {
        List<ThreadDescriptor> descriptors = cache.getThreadDescriptors();
        descriptors.forEach(value -> saver.saveThreadDescriptor(identifier, value));
        ThreadsInfo info = cache.getThreadsInfo();
        saver.saveThreadsInfo(identifier, info);
        return true;
    }
}
