package com.my.home.task;

/**
 *
 */
public interface IAppTask<V>
{
    /**
     * Determine if task is ready to execute
     * @return - true is ready
     */
    boolean toExecute();

    /**
     * Determine is task has to be removed from execution
     * @return - true if need to remove task
     */
    boolean toRemove();

    /**
     * Execute this method when task is ready to execute
     */
    void execute();

}
