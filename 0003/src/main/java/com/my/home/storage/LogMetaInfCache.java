package com.my.home.storage;

import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadEntry;
import com.my.home.log.beans.ThreadsInfo;

import java.util.*;

/**
 *
 */
public class LogMetaInfCache
{
    private Map<String, ThreadDescriptor> cache;
    public LogMetaInfCache()
    {
        cache = new LinkedHashMap<>();
    }
    void cacheMetaInf(LogNode node)
    {
        String threadKey = node.getThread();
        String nodeFileName = String.valueOf(node.getId());
        ThreadDescriptor descriptor;
        if (cache.containsKey(threadKey))
        {
            descriptor = cache.get(threadKey);
        }
        else
        {
            descriptor = new ThreadDescriptor();
            descriptor.setName(threadKey);
            descriptor.setStartTime(node.getLongDateTime());
            cache.put(threadKey, descriptor);
            descriptor.setId(Long.valueOf(cache.size()));
        }
        descriptor.getNodesNumbers().add(nodeFileName);
        descriptor.setEndTime(node.getLongDateTime());
    }
    ThreadsInfo getThreadsInfo()
    {
        ThreadsInfo threadsInfo = new ThreadsInfo();
        ThreadEntry threadEntry;
        String id;
        boolean result = true;
        for (Map.Entry<String, ThreadDescriptor> entry : cache.entrySet())
        {
            id = String.valueOf(entry.getValue().getId());
            threadEntry = new ThreadEntry();
            threadEntry.setFileName(id);
            threadEntry.setThreadName(entry.getKey());
            threadEntry.setStartTime(entry.getValue().getStartTime());
            threadEntry.setEndTime(entry.getValue().getEndTime());
            threadEntry.setIsDelete(false);
            threadsInfo.getThreads().add(threadEntry);
        }
        Collections.sort(threadsInfo.getThreads(), (o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime()));
        return threadsInfo;
    }
    List<ThreadDescriptor> getThreadDescriptors()
    {
        final List<ThreadDescriptor> out = new ArrayList<>();
        cache.values().forEach(value -> out.add(value));
        return out;
    }

}
