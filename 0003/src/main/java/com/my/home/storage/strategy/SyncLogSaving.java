package com.my.home.storage.strategy;

import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.LogMetaInfCache;

/**
 *
 */
public class SyncLogSaving extends AbstractSavingStrategy
{

    private final ILogSaver saver;

    public SyncLogSaving(ILogSaver saver)
    {
        this.saver = saver;
    }

    @Override
    public void saving(ILogIdentifier identifier, LogNode node)
    {
        //  Before saving log node we need to cache meta inf
        cachingMetaInf(node);
        saver.saveNode(identifier, node);
    }

    @Override
    public void invalidate(ILogIdentifier identifier)
    {
        saveMetaInf(identifier, saver);
        saver.complete(identifier);
    }
}
