package com.my.home.beans;

import com.my.home.storage.ILogNodeParser;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.ILogStorageContext;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 25.06.17
 * Time: 7:47
 * To change this template use File | Settings | File Templates.
 */
public class LogStorageTestContext implements ILogStorageContext
{
    private ILogNodeParser parser;
    private ILogSaver saver;

    public void setParser(ILogNodeParser parser) {
        this.parser = parser;
    }

    public void setSaver(ILogSaver saver) {
        this.saver = saver;
    }

    @Override
    public ILogNodeParser getParser() {
        return parser;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ILogSaver getSaver() {
        return saver;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
