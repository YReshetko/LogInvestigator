package com.my.home.storage;

import com.my.home.processor.ILogProgress;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 23.06.17
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public interface ILogStorageContext
{
    ILogNodeParser getParser();
    ILogSaver getSaver();
    ILogRetriever getRetriever();
    ILogProgress getProgress();
}
