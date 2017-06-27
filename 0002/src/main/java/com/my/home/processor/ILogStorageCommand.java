package com.my.home.processor;

import com.my.home.storage.ILogIdentifier;

/**
 *  Command defines how the storage should work with some data
 */
public interface ILogStorageCommand<V>
{
    void setData(V... value);
    void setData(ILogIdentifier identifier, V... value);
    void setData(ILogIdentifier identifier);
    ILogIdentifier getIdentifier();
    String getCommand();

}
