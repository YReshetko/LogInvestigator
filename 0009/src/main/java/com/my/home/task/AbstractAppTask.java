package com.my.home.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 */
public abstract class AbstractAppTask<V> implements IAppTask<V>
{
    private Future<V> future;
    private boolean remove;
    public AbstractAppTask(Future<V> future)
    {
        this.future = future;
        remove = false;
    }

    /**
     * Determine if task is ready to execute
     *
     * @return - true is ready
     */
    @Override
    public boolean toExecute()
    {
        boolean result;
        if (future.isCancelled())
        {
            remove = true;
            result = false;
        }
        else
        {
            result = !remove && future.isDone();
        }
        return result;
    }

    /**
     * Determine is task has to be removed from execution
     *
     * @return - true if need to remove task
     */
    @Override
    public boolean toRemove()
    {
        return remove;
    }

    /**
     * Execute this method when task is ready to execute
     */
    @Override
    public void execute()
    {
        try
        {
            V result = future.get();
            executeResult(result);
        }
        catch (InterruptedException|ExecutionException e)
        {
            e.printStackTrace();
        }
        finally
        {
            remove = true;
        }
    }

    protected abstract void executeResult(V result);
}
