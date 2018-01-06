package com.my.home.storage.strategy;

import com.my.home.log.beans.LogNode;
import com.my.home.storage.ILogIdentifier;
import com.my.home.storage.ILogSaver;
import com.my.home.storage.LogMetaInfCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class AsyncLogSaving extends AbstractSavingStrategy
{
    private final ILogSaver saver;
    private final ExecutorService executor;

    public AsyncLogSaving(ILogSaver saver)
    {
        this.saver = saver;
        this.executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public void saving(ILogIdentifier identifier, LogNode node)
    {
        //  Before saving log node we need to cache meta inf
        cachingMetaInf(node);
        executor.execute(new SimpleNodeSaverProcess(identifier, node));
    }

    @Override
    public void invalidate(ILogIdentifier identifier) {
        executor.shutdown();
        //  Wait all threads
        log("Waiting for termination of thread pull");
        while (!executor.isTerminated())
        {
        }
        log("Saving metainf");
        saveMetaInf(identifier, saver);
        saver.complete(identifier);
    }

    private class SimpleNodeSaverProcess implements Runnable
    {
        private ILogIdentifier identifier;
        private LogNode node;
        public SimpleNodeSaverProcess(ILogIdentifier identifier, LogNode node)
        {
            this.identifier = identifier;
            this.node = node;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            saver.saveNode(identifier, node);
        }
    }
}
