package com.my.home.storage.mongo.impl;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.my.home.storage.mongo.IMongoLogAccess;
import com.my.home.util.JsonUtils;

import java.util.Iterator;

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
    public MongoLogAccess(DBCollection collection, Class<V> aClass)
    {
        this.collection = collection;
        this.aClass = aClass;
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
    public void setSortBy(String field) {
        sortBy = field;
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
        public LogIterator(DBCursor cursor, Class<V> vClass)
        {
            this.cursor = cursor;
            this.vClass = vClass;
        }
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws java.util.NoSuchElementException
         *          if the iteration has no more elements
         */
        @Override
        public V next() {

            return JsonUtils.getObject(JSON.serialize(cursor.next()), vClass);
        }
    }
}
