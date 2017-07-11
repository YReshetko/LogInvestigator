package com.my.home.log.manager;

import com.my.home.log.beans.ThreadsInfo;
import com.my.home.storage.ILogIdentifier;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class MainLogManager
{
    private List<ILogManager> logManagers;
    private ILogIdentifier identifier;
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

    public List<String> getSelectedThreads()
    {
        Set<String> threads = new HashSet<>();
        logManagers.forEach(manager -> threads.addAll(manager.getSelectedThreads()));
        return threads.stream().collect(Collectors.toList());
    }
    public ILogIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(ILogIdentifier identifier) {
        this.identifier = identifier;
    }
}
