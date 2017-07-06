package com.my.home.processor;

import com.my.home.storage.ILogIdentifier;
import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogStorageContext;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

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
    Future<ILogIdentifier> process(ILogIdentifier identifier, List<File> files);

    /**
     * Retrieves iterator of log nodes for command
     * @param identifier - identifier of log
     * @param command - determines what log we need to retrieve
     * @return - list of log nodes
     */
    <V> Iterator<V> getIterator(ILogIdentifier identifier, ILogStorageCommand<V> command);

    /**
     *
     * @param identifier
     * @param command
     * @return
     */
    String getLog(ILogIdentifier identifier, ILogStorageCommand<LogNode> command);

    /**
     * Use for updating, removing etc
     * @param identifier - identifier of log
     * @param command - command to execute
     * @return - true if success
     */
    boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command);

    /**
     * Setup storage context
     * @param context - context, contains implementations of parser, saver, retriever etc
     */
    void setStorageContext(ILogStorageContext context);
}
