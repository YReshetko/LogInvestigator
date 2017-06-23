package com.my.home;

import com.my.home.log.beans.LogNode;

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
    boolean save(ILogIdentifier identifier, LogNode value);

    /**
     * Finish save (save info files)
     * @param identifier - log identifier
     * @param files - log identifier
     * @return - true if saving was successfully
     */
    boolean complete(ILogIdentifier identifier, List<File> files);
}
