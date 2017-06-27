package com.my.home.storage.mongo.wrapper;

import com.my.home.storage.ILogIdentifier;
import com.my.home.util.JsonUtils;

/**
 *
 */
public class WrapperToSave<V>
{

    private V value;
    private String key;
    public WrapperToSave(ILogIdentifier identifier, V value)
    {
        this.key = identifier.getKey();
        this.value = value;
    }

    public String getRecordToSave()
    {
        //  TODO Double conversion from/to JSON (2)
        String val = JsonUtils.getJson(value);
        return String.format(DBRequestTemplates.TO_SAVE, key, val);
    }


}
