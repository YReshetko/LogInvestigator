package com.my.home.storage;

import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.processor.ILogStorageCommand;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public interface ILogRetriever
{
    <V> Iterator<V> get(ILogIdentifier identifier, ILogStorageCommand<V> command);
    void changeLog(ILogIdentifier identifier, ILogStorageCommand command);
}
