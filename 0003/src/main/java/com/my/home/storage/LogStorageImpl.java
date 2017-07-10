package com.my.home.storage;

import com.my.home.log.beans.LogBlock;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.processor.ILogProgress;
import com.my.home.processor.ILogStorage;
import com.my.home.processor.ILogStorageCommand;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 *
 */
public class LogStorageImpl implements ILogStorage
{
    private static final String NEW_LINE = "\n";
    private ILogStorageContext context;
    private ILogNodeParser parser;
    private ILogSaver saver;
    private ILogRetriever retriever;
    private ILogProgress progress;
    @Override
    public Future<ILogIdentifier> process(ILogIdentifier identifier, List<File> files)
    {
        Callable<ILogIdentifier> parseLog = new ParseLogProcess(context, files, identifier);
        FutureTask<ILogIdentifier> future = new FutureTask<ILogIdentifier>(parseLog);
        new Thread(future).start();
        return future;
    }

    @Override
    public void setStorageContext(ILogStorageContext iLogStorageContext)
    {
        this.context = iLogStorageContext;
        this.parser = context.getParser();
        this.saver = context.getSaver();
        this.retriever = context.getRetriever();
        this.progress = context.getProgress();
    }

    @Override
    public <V> Iterator<V> getIterator(ILogIdentifier identifier, ILogStorageCommand<V> command)
    {
        command.setData(identifier);
        return retriever.get(identifier, command);
    }

    @Override
    public String getLog(ILogIdentifier identifier, ILogStorageCommand<LogNode> iLogStorageCommand)
    {
        Iterator<LogNode> nodes = getIterator(identifier, iLogStorageCommand);
        StringBuilder buffer = new StringBuilder();
        while (nodes.hasNext())
        {
            buffer.append(parser.parse(nodes.next()));
            buffer.append(NEW_LINE);
        }
        return buffer.toString();
    }

    @Override
    public boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command)
    {
        retriever.changeLog(identifier, command);
        return true;
    }


}
