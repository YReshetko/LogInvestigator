package com.my.home.log.manager.web;

/**
 * Interface to get callbacks from JavaScript for Threads view
 */
public interface IWebJsLogManager
{
    void download(String value);
    void addSelectedThread(String threadName);
}
