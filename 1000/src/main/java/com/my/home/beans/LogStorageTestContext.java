package com.my.home.beans;

import com.my.home.storage.ILogNodeParser;
import com.my.home.storage.ILogRetriever;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.ILogStorageContext;

/**
 * Test context
 */
public class LogStorageTestContext implements ILogStorageContext
{
    private ILogNodeParser parser;
    private ILogSaver saver;
    private ILogRetriever retriever;

    public void setParser(ILogNodeParser parser) {
        this.parser = parser;
    }

    public void setSaver(ILogSaver saver) {
        this.saver = saver;
    }
    public void setRetriever(ILogRetriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public ILogNodeParser getParser() {
        return parser;
    }

    @Override
    public ILogSaver getSaver() {
        return saver;
    }

    @Override
    public ILogRetriever getRetriever() {
        return retriever;
    }
}
