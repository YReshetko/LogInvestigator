package com.my.home.util.download;

import com.my.home.log.LogNodeParser;
import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogProgress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Processor to save log into file
 */
public class SaveLogIntoFile implements Callable<File>
{
    private static final String NEW_LINE = "\n";
    private Iterator<LogNode> nodes;
    private File fileToSave;
    private LogNodeParser parser;
    private ILogProgress progress;
    private long size;
    public SaveLogIntoFile(Iterator<LogNode> nodes, File fileToSave, LogNodeParser parser, ILogProgress progress, long size)
    {
        this.nodes = nodes;
        this.fileToSave = fileToSave;
        this.parser = parser;
        this.progress = progress;
        this.size = size;
    }
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public File call() throws Exception
    {
        FileWriter writer = null;
        progress.setTotalSize(this.size);
        try
        {
            if(fileToSave != null)
            {
                writer = new FileWriter(fileToSave);
                StringBuffer buffer;
                while (nodes.hasNext())
                {
                    LogNode node = nodes.next();
                    buffer = new StringBuffer();
                    buffer.append(parser.parse(node));
                    buffer.append(NEW_LINE);
                    writer.write(buffer.toString());
                    writer.flush();
                    progress.subtractSize(1L);
                }
            }
        }
        catch (IOException e)
        {
            fileToSave = null;
            e.printStackTrace();
        }
        finally
        {
            if(writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
                {
                    fileToSave = null;
                    e.printStackTrace();
                }
            }
        }
        return fileToSave;
    }
}
