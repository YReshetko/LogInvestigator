package com.my.home.storage.mongo;


import java.util.Iterator;

/**
 *
 */
public interface IMongoLogAccess<V>
{
    <V> Iterator<V> findAll();
    <V> Iterator<V> find(String searchKey);

    void insert(String value);

    void update(String oldValue, String newValue);
    void indexing(String keys);
    void removeAll();
    void remove(String value);
    void setSortBy(String field);
}
