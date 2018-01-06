package com.my.home.storage.mongo.impl;

import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.mongo.IMongoLogAccess;
import com.my.home.util.JsonUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 06.01.18
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class BufferedMongoLogSaver extends MongoLogSaver
{

    private static final int BUFFER_SIZE = 1000;
    private int currentBuffer;
    public BufferedMongoLogSaver(MongoConnection connection)
    {
        super(connection);
        currentBuffer = 0;
    }
    @Override
    protected <V> boolean save(ILogIdentifier identifier, V value)
    {
        IMongoLogAccess access = getAccess(identifier, getCollection(value.getClass()));
        if (value instanceof LogNode)
        {
            access.add(JsonUtils.getJson(value));
            currentBuffer++;
            if(currentBuffer >= BUFFER_SIZE)
            {
                access.flush();
            }
        }
        else
        {
            access.insert(JsonUtils.getJson(value));
        }

        return true;
    }
    @Override
    public boolean complete(ILogIdentifier identifier)
    {
        if(currentBuffer > 0)
        {
            IMongoLogAccess access = getAccess(identifier, getCollection(LogNode.class));
            access.flush();
        }
        return super.complete(identifier);
    }
}
