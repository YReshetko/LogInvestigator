package com.my.home.processor;

import com.my.home.storage.ILogIdentifier;

/**
 *  Command defines how the storage should work with some data
 */
public interface ILogStorageCommand<V>
{
    enum Command
    {
        FIND, FIND_ALL, UPDATE, REMOVE, REMOVE_ALL;
    }
    void setData(V... value);
    void setData(ILogIdentifier identifier, V... value);
    void setData(ILogIdentifier identifier);
    ILogIdentifier getIdentifier();
    String sortBy();
    Class<V> getType();
    Command getCommandType();
    String getSelector();
    String getOldValue();
    String getNewValue();


}
