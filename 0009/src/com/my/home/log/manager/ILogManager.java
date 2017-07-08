package com.my.home.log.manager;

import com.my.home.log.beans.ThreadsInfo;

import java.util.List;

/**
 *
 */
public interface ILogManager
{
    void setThreadsInfo(ThreadsInfo value);
    List<String> getSelectedThreads();
}
