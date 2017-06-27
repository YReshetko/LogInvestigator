package com.my.home.storage.mongo.impl;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.my.home.storage.mongo.IMongoAccess;
import com.my.home.util.JsonUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;

/**
 *
 */
public class MongoAccess<V> implements IMongoAccess<V>
{

    private DBCollection collection;
    private Class aClass;
    public MongoAccess(DBCollection collection, Class aClass)
    {
        this.collection = collection;
        this.aClass = aClass;
    }

    @Override
    public <V> Iterator<V> findAll() {
        return new LogIterator(collection.find(), aClass);
    }

    @Override
    public <V> Iterator<V> find(String searchKey)
    {
        return new LogIterator(collection.find((DBObject) JSON.parse(searchKey)), aClass);
    }

    @Override
    public void insert(String value) {
        //  TODO Double conversion from/to JSON (1)
        DBObject toInsert = (DBObject) JSON.parse(value);
        collection.insert(toInsert);
    }

    @Override
    public void update(String oldValue, String newValue) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void remove(String value) {

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
