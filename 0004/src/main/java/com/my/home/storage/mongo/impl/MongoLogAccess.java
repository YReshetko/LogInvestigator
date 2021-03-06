package com.my.home.storage.mongo.impl;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.my.home.storage.mongo.IMongoLogAccess;
import com.my.home.util.JsonUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
public class MongoLogAccess<V> implements IMongoLogAccess<V>
{

    private final static String SORT_TEMPLATE = "{\"id\" : 1}";
    private DBCollection collection;
    private DBObject sort;
    private Class<V> aClass;
    private String sortBy;
    private final ReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;

    private List<DBObject> buffer;
    public MongoLogAccess(DBCollection collection, Class<V> aClass)
    {
        this.collection = collection;
        this.aClass = aClass;
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        buffer = new LinkedList<>();
    }

    @Override
    public <V> Iterator<V> findAll() {
        DBCursor cursor = null;
        sort = getSorter();
        if(sort == null)
        {
            cursor = collection.find();
        }
        else
        {
            cursor = collection.find().sort(sort);
        }
        return new LogIterator(cursor, aClass);
    }

    @Override
    public <V> Iterator<V> find(String searchKey)
    {
        DBCursor cursor = null;
        sort = getSorter();
        if(sort == null)
        {
            cursor = collection.find((DBObject) JSON.parse(searchKey));
        }
        else
        {
            cursor = collection.find((DBObject) JSON.parse(searchKey)).sort(sort);
        }
        return new LogIterator(cursor, aClass);
    }

    @Override
    public <V> Iterator<V> find(List<String> searchKey)
    {
        DBCursor cursor = null;
        sort = getSorter();
        return new LogIterator(searchKey, collection, sort, aClass);
    }


    @Override
    public void insert(String value) {
        //  TODO Double conversion from/to JSON (1)
        DBObject toInsert = (DBObject) JSON.parse(value);
        collection.insert(toInsert);
    }

    @Override
    public void update(String oldValue, String newValue) {
        DBObject toUpdate = (DBObject) JSON.parse(oldValue);
        DBObject value = (DBObject) JSON.parse(newValue);
        collection.update(toUpdate, value);
    }

    @Override
    public void removeAll() {

    }

    @Override
    public void remove(String value) {

    }
    @Override
    public void indexing(String keys)
    {
        DBObject dbKey = (DBObject) JSON.parse(keys);
        collection.createIndex(dbKey);
    }

    @Override
    public void setSortBy(String field) {
        sortBy = field;
    }

    /**
     * Methods used with buffered saving
     *
     * @param value - document to save
     */
    @Override
    public void add(String value)
    {
        DBObject toAdd = (DBObject) JSON.parse(value);
        writeLock.lock();
            buffer.add(toAdd);
        writeLock.unlock();
    }

    /**
     * Send all collected documents to Mongo DB
     *
     * @return - true if it's completed
     */
    @Override
    public boolean flush()
    {
        writeLock.lock();
        List<DBObject> toInsert = buffer;
        buffer = new LinkedList<>();
        writeLock.unlock();
        if (!toInsert.isEmpty())
        {
            collection.insert(toInsert);
        }
        return true;
    }

    private DBObject getSorter()
    {
        if (sortBy != null)
        {
            return (DBObject) JSON.parse(String.format(SORT_TEMPLATE, sortBy));
        }
        else
        {
            return null;
        }
    }

    private class LogIterator<V> implements Iterator<V>
    {

        private DBCursor cursor;
        private Class<V> vClass;
        private Iterator<String> selectors;
        private DBCollection collection;
        private DBObject sort;
        public LogIterator(DBCursor cursor, Class<V> vClass)
        {
            this.cursor = cursor;
            this.vClass = vClass;
        }

        public LogIterator(List<String> selectors, DBCollection collection, DBObject sort, Class<V> vClass)
        {
            this.vClass = vClass;
            this.selectors = (selectors != null)?selectors.iterator():null;
            this.collection = collection;
            this.sort = sort;
        }
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext()
        {
            boolean hasNext = false;
            if (cursor != null)
            {
                hasNext = cursor.hasNext();
            }
            if (!hasNext)
            {
                if(selectors != null && selectors.hasNext())
                {
                    String selector = selectors.next();
                    if(sort == null)
                    {
                        cursor = collection.find((DBObject) JSON.parse(selector));
                    }
                    else
                    {
                        cursor = collection.find((DBObject) JSON.parse(selector)).sort(sort);
                    }
                    hasNext = cursor.hasNext();
                }
            }

            return hasNext;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws java.util.NoSuchElementException
         *          if the iteration has no more elements
         */
        @Override
        public V next()
        {
            DBObject retrieved = cursor.next();
            return JsonUtils.getObject(JSON.serialize(retrieved), vClass);
        }
    }
}
