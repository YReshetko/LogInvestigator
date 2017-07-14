package com.my.home.storage.strategy;

import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.LogMetaInfCache;

/**
 *
 */
public interface ILogSavingStrategy
{
    void saving(ILogIdentifier identifier, LogNode node);
    void invalidate(ILogIdentifier identifier);
}
