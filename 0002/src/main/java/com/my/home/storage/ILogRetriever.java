package com.my.home.storage;

import com.my.home.processor.ILogStorageCommand;

import java.util.Iterator;

/**
 *
 */
public interface ILogRetriever
{
    <V> Iterator<V> get(ILogIdentifier identifier, ILogStorageCommand<V> command);
    void changeLog(ILogIdentifier identifier, ILogStorageCommand command);
}
