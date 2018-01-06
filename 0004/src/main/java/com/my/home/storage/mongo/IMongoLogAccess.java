package com.my.home.storage.mongo;


import java.util.Iterator;
import java.util.List;

/**
 *
 */
public interface IMongoLogAccess<V>
{
    <V> Iterator<V> findAll();
    <V> Iterator<V> find(String searchKey);
    <V> Iterator<V> find(List<String> searchKey);

    void insert(String value);

    void update(String oldValue, String newValue);
    void indexing(String keys);
    void removeAll();
    void remove(String value);
    void setSortBy(String field);

    /**
     * Methods used with buffered saving
     * @param value - document to save
     */
    void add(String value);

    /**
     * Send all collected documents to Mongo DB
     * @return - true if it's completed
     */
    boolean flush();
}
