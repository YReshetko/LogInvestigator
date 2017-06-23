package com.my.home.processor;

import com.my.home.storage.ILogIdentifier;
import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogStorageContext;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Determines list of method which we can use for work with log nodes
 */
public interface ILogStorage
{
    void process(ILogIdentifier identifier, List<File> files);
    Iterator<LogNode> getIterator(ILogIdentifier identifier, ILogStorageCommand command);
    boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command);

    void setStorageContext(ILogStorageContext context);
}
