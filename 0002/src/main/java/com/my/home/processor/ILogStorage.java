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
    /**
     * Parses log files and save it into storage
     * @param identifier - identifier of log
     * @param files - list of log files
     */
    void process(ILogIdentifier identifier, List<File> files);

    /**
     * Retrieves iterator of log nodes for command
     * @param identifier - identifier of log
     * @param command - determines what log we need to retrieve
     * @return - list of log nodes
     */
    Iterator<LogNode> getIterator(ILogIdentifier identifier, ILogStorageCommand command);

    /**
     *
     * @param identifier
     * @param command
     * @return
     */
    boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command);

    void setStorageContext(ILogStorageContext context);
}
