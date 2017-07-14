package com.my.home.storage;

import com.my.home.log.beans.LogBlock;
import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogProgress;
import com.my.home.storage.strategy.AsyncLogSaving;
import com.my.home.storage.strategy.ILogSavingStrategy;
import com.my.home.storage.strategy.SyncLogSaving;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 */
public class ParseLogProcess implements Callable<ILogIdentifier>
{

    private final ILogNodeParser parser;
    private final ILogProgress progress;
    private final ILogIdentifier identifier;
    private final ILogSavingStrategy savingStrategy;
    private List<File> files;
    public ParseLogProcess(ILogStorageContext context, List<File> files, ILogIdentifier identifier)
    {
        this.parser = context.getParser();
        this.progress = context.getProgress();
        SavingStrategyType savingType = SavingStrategyType.valueOf(context.getSavingStrategy());
        savingStrategy = savingType.getStrategy(context.getSaver());
        this.identifier = identifier;
        this.files = files;


    }
    @Override
    public ILogIdentifier call()
    {
        try {
            List<LogBlock> blocks = getLogBlocks(files);
            //  We need to sort LogBlocks by time to save all parts of log
            Collections.sort(blocks, (o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime()));
            //  Calculate total size of all files (for future to make it as thread)
            long totalSize = blocks.stream().mapToLong(LogBlock::getSize).sum();
            progress.addTotalSize(totalSize);
            //Process of saving log nodes
            BufferedReader br = null;
            String line;
            StringBuilder logBuffer = null;
            boolean result = true;
            long index = 0L;
            for (LogBlock block : blocks)
            {
                String file = block.getFileName();
                // TODO investigate how to read file via NIO
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null)
                {
                    if (parser.hasStamp(line))
                    {
                        if (logBuffer != null)
                        {
                            index++;
                            result &= saveOneNode(logBuffer.toString(), identifier, index);
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
                    progress.subtractSize(line.length());
                }
            }
            index++;
            if (logBuffer != null)
            {
                result &= saveOneNode(logBuffer.toString(), identifier, index);
            }
            if (result)
            {
                savingStrategy.invalidate(identifier);
            }

        }
        catch (ParseException | IOException e)
        {
            e.printStackTrace();
        }
        /*finally
        {
            savingStrategy.invalidate();
        }*/
        return identifier;
    }

    /**
     * Save one node
     * @param line - string for node
     * @param identifier - log identifier
     * @param index - node index
     * @return - true if saving has success
     * @throws ParseException - unexpected parsing exception
     */
    private boolean saveOneNode(String line, ILogIdentifier identifier, Long index) throws ParseException
    {
        LogNode node = parser.parse(line);
        node.setId(index);
        savingStrategy.saving(identifier, node);
        return true;
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

    private enum SavingStrategyType
    {
        ASYNC
                {
            @Override
            public ILogSavingStrategy getStrategy(ILogSaver saver)
            {
                return new AsyncLogSaving(saver);
            }
        },
        SYNC
                {
        @Override
        public ILogSavingStrategy getStrategy(ILogSaver saver)
        {
            return new SyncLogSaving(saver);
        }
    };
        public abstract ILogSavingStrategy getStrategy(ILogSaver saver);
    }
}
