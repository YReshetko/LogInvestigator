package com.my.home.storage.mongo.commands;

import com.my.home.processor.ILogStorageCommand;
import com.my.home.storage.ILogIdentifier;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class AbstractStorageCommand<V> implements ILogStorageCommand<V>
{
    private static final String JSON_SEPARATOR = ",";

    private ILogIdentifier identifier;
    protected List<V> value;



    @Override
    public void setData(V... vs) {
        if (vs == null || vs.length == 0 || vs[0] == null)
        {
            throw new IllegalArgumentException("The value of command should not be null or empty");
        }
        value =  Arrays.asList(vs);
        try
        {
            value.forEach(this::checkValues);
        }
        catch (IllegalArgumentException e)
        {
            value = null;
            throw new IllegalArgumentException(e);
        }

    }
    private void checkValues(V value)
    {
        if(value == null)
        {
            throw new IllegalArgumentException("The value of command should not be null or empty");
        }
    }
    @Override
    public void setData(ILogIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void setData(ILogIdentifier identifier, V... vs) {
        setData(vs);
        setData(identifier);
    }

    @Override
    public ILogIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String sortBy() {
        return null;
    }

    protected String split(List<String> buff)
    {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<buff.size() - 1; i++)
        {
            builder.append(buff.get(i)).append(JSON_SEPARATOR);
        }
        builder.append(buff.get(buff.size() - 1));
        return builder.toString();
    }
}
