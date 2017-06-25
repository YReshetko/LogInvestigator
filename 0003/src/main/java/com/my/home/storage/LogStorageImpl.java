package com.my.home.storage;

import com.my.home.log.beans.LogBlock;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadDescriptor;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.processor.ILogStorage;
import com.my.home.processor.ILogStorageCommand;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 *
 */
public class LogStorageImpl implements ILogStorage
{
    private ILogNodeParser parser;
    private ILogSaver saver;
    @Override
    public void process(ILogIdentifier identifier, List<File> files)
    {
        try {
            List<LogBlock> blocks = getLogBlocks(files);
            //  We need to sort LogBlocks by time to save all parts of log
            Collections.sort(blocks, (o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime()));
            //  Calculate total size of all files (for future to make it as thread)
            long totalSize = blocks.stream().mapToLong(value -> value.getSize()).sum();


            //Process of saving log nodes
            BufferedReader br = null;
            String line;
            StringBuilder logBuffer = null;
            LogNode node;
            LogMetaInfCache cache = new LogMetaInfCache();
            boolean result = true;
            long index = 0L;
            for (LogBlock block : blocks)
            {
                String file = block.getFileName();
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null)
                {
                    if (parser.hasStamp(line))
                    {
                        if (logBuffer != null)
                        {
                            index++;
                            result &= saveOneNode(logBuffer.toString(), identifier, index, cache);
                        }
                        logBuffer = new StringBuilder(line);
                    }
                    else
                    {
                        if(logBuffer != null)
                        {
                            logBuffer.append("\n");
                            logBuffer.append(line);
                        }
                    }
                }
            }
            index++;
            result &= saveOneNode(logBuffer.toString(), identifier, index, cache);
            if (result)
            {
                result &= saveMetaInf(identifier, cache);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ParseException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Save one node
     * @param line - string for node
     * @param identifier - log identifier
     * @param index - node index
     * @param cache - cache of nodes metainf
     * @return - true if saving has success
     * @throws ParseException - unexpected parsing exception
     */
    private boolean saveOneNode(String line, ILogIdentifier identifier, Long index, LogMetaInfCache cache) throws ParseException
    {
        LogNode node = parser.parse(line);
        node.setId(index);
        //  Before saving log node we need to cache meta inf
        cache.cacheMetaInf(node);
        return saver.saveNode(identifier, node);
    }

    /**
     * Save meta information about log (threads info)
     * @param identifier - log identifier
     * @param cache - cache where was collected meta inf
     * @return - true if saving has success
     */
    private boolean saveMetaInf(ILogIdentifier identifier, LogMetaInfCache cache)
    {
        List<ThreadDescriptor> descriptors = cache.getThreadDescriptors();
        descriptors.forEach(value -> saver.saveThreadDescriptor(identifier, value));
        ThreadsInfo info = cache.getThreadsInfo();
        saver.saveThreadsInfo(identifier, info);
        return true;
    }

    @Override
    public void setStorageContext(ILogStorageContext iLogStorageContext) {
        this.parser = iLogStorageContext.getParser();
        this.saver = iLogStorageContext.getSaver();
    }

    @Override
    public Iterator<LogNode> getIterator(ILogIdentifier identifier, ILogStorageCommand command) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * LogBlock contains log file name and first time in log to determine correct sequence of files
     * to make correct nodes if parts of node are contained in different files
     * @param files - contain list non-sorted files name
     * @return - list of LogBlocks
     * @throws Exception - any exception
     */
    private List<LogBlock> getLogBlocks(List<File> files)
    {
        List<LogBlock> out = new ArrayList<LogBlock>();
        BufferedReader br = null;
        try {
            String line = "";
            LogBlock logBlock = null;
            for (File file : files)
            {
                logBlock = new LogBlock();
                logBlock.setFileName(file.getAbsolutePath());
                logBlock.setSize(file.length());
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null)
                {
                    if(parser.hasStamp(line))
                    {
                        LogNode node = parser.parse(line);
                        logBlock.setStartTime(node.getLongDateTime());
                        br.close();
                        break;
                    }

                }
                br.close();
                out.add(logBlock);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error creation log blocks", e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error reading log files for log blocks", e);
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return out;
    }
}
