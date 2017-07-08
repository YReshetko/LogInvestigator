package com.my.home.log.manager;

import com.my.home.log.beans.ThreadsInfo;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class MainLogManager
{
    private List<ILogManager> logManagers;
    public MainLogManager()
    {
        logManagers = new LinkedList<>();
    }
    public void addLogManger(ILogManager logManager)
    {
        logManagers.add(logManager);
    }

    public void setThreadsInfo(ThreadsInfo info)
    {
        logManagers.forEach(manager -> manager.setThreadsInfo(info));
    }
}
