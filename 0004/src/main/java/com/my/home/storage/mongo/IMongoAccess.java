package com.my.home.storage.mongo;

import java.util.Iterator;

/**
 *
 */
public interface IMongoAccess
{
    Iterator<String> findAll();
    Iterator<String> find(String searchKey);

    void insert(String value);

    void update(String oldValue, String newValue);

    void removeAll();
    void remove(String value);
}
