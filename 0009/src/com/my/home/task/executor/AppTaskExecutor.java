package com.my.home.task.executor;

import com.my.home.task.IAppTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 *
 */
public class AppTaskExecutor extends Thread
{
    private final List<IAppTask> tasks;
    private final ReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    private boolean isWait;
    private final Object monitor;
    public AppTaskExecutor()
    {
        tasks = new LinkedList<>();
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        isWait = false;
        monitor = new Object();
        this.start();
    }
    @Override
    public void run()
    {
        while (!this.isInterrupted())
        {
            if (tasks.size() > 0)
            {
                List<IAppTask> tasksToRemove = null;
                List<IAppTask> tasksToExecute = null;

                readLock.lock();
                try
                {
                    tasksToRemove = tasks.stream().filter(IAppTask::toRemove).collect(Collectors.toList());
                    tasksToExecute = tasks.stream().filter(IAppTask::toExecute).collect(Collectors.toList());
                }
                finally
                {
                    readLock.unlock();
                }
                tasksToExecute.stream().forEach(IAppTask::execute);
                writeLock.lock();
                try
                {
                    tasks.removeAll(tasksToRemove);
                }
                finally
                {
                    writeLock.unlock();
                }
            }
            else
            {
                System.out.println("Tasks executor: Waiting new tasks");
                isWait = true;
                try
                {
                    synchronized (monitor)
                    {
                        monitor.wait();
                    }
                    isWait = false;
                    System.out.println("Tasks executor: Wake up");

                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }


    }

    public void addTask(IAppTask task)
    {
        writeLock.lock();
        try
        {
            tasks.add(task);
        }
        finally
        {
            writeLock.unlock();
            if (isWait)
            {
                synchronized (monitor)
                {
                    monitor.notify();
                }
            }
        }
    }

}
