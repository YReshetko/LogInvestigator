package com.my.home.beans;

import com.my.home.storage.ILogIdentifier;

/**
 *
 */
public class TestLogIdentifier implements ILogIdentifier
{
    private final String key;
    public TestLogIdentifier(String key)
    {
        this.key = key;
    }
    @Override
    public String getKey() {
        return key;
    }
}
