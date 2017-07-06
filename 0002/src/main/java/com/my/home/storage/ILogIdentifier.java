package com.my.home.storage;

import com.my.home.log.beans.LogFilesDescriptor;

/**
 * Simply it's log ID which help to determine log defenetaly
 */
public interface ILogIdentifier
{
    String getKey();
    LogFilesDescriptor getLogDescriptor();
}
