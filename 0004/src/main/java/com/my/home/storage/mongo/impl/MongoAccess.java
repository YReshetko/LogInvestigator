package com.my.home.storage.mongo.impl;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.my.home.storage.mongo.IMongoAccess;

import java.util.Iterator;

/**
 *
 */
public class MongoAccess implements IMongoAccess
{

    private DBCollection collection;
    public MongoAccess(DBCollection collection)
    {
        this.collection = collection;
    }

    @Override
    public Iterator<String> findAll() {
        return null;
    }

    @Override
    public Iterator<String> find(String searchKey) {
        return null;
    }

    @Override
    public void insert(String value) {
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
}
