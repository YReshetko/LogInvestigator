package com.my.home.storage;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;

import java.io.File;
import java.util.List;

/**
 *
 */
public interface ILogSaver
{
    /**
     * Save log node
     * @param value - log node
     * @param identifier - log identifier
     * @return - true if log node was saved successfully
     */
    boolean saveNode(ILogIdentifier identifier, LogNode value);

    /**
     * Save threads info
     * @param value - log threads info
     * @param identifier - log identifier
     * @return - true if log node was saved successfully
     */
    boolean saveThreadsInfo(ILogIdentifier identifier, ThreadsInfo value);

    /**
     * Save thread descriptor
     * @param value - log thread descriptor
     * @param identifier - log identifier
     * @return - true if log node was saved successfully
     */
    boolean saveThreadDescriptor(ILogIdentifier identifier, ThreadDescriptor value);
    /**
     * Finish save (save info files)
     * @param identifier - log identifier
     * @param files - log identifier
     * @return - true if saving was successfully
     */
    boolean complete(ILogIdentifier identifier, List<File> files);
}
