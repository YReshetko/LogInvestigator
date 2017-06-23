package com.my.home.mongo;

import com.my.home.*;
import com.my.home.log.beans.LogBlock;
import com.my.home.log.beans.LogNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 *
 */
public class MongoLogStorage implements ILogStorage
{
    private ILogNodeParser parser;
    private ILogSaver saver;
    @Override
    public void process(ILogIdentifier identifier, List<File> files)
    {
        List<LogBlock> blocks = getLogBlocks(files);
        //  We need to sort LogBlocks by time to save all parts of log
        Collections.sort(blocks, (o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime()));
        long totalSize = blocks.stream().mapToLong(value -> value.getSize()).sum();

    }

    @Override
    public Iterator<LogNode> getIterator(ILogIdentifier identifier, ILogStorageCommand command) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean changeLog(ILogIdentifier identifier, ILogStorageCommand command) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public void setParser(ILogNodeParser parser) {
        this.parser = parser;
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
