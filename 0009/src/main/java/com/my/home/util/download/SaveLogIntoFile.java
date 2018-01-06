package com.my.home.util.download;

import com.my.home.BaseLogger;
import com.my.home.log.LogNodeParser;
import com.my.home.log.beans.LogNode;
import com.my.home.processor.ILogProgress;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Processor to save log into file
 */
public class SaveLogIntoFile extends BaseLogger implements Callable<File>
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
        long time = System.currentTimeMillis();
        NIOBufferedSaving();
        time = System.currentTimeMillis() - time;
        log("Time taken - " + time + "ms.");
        return fileToSave;
    }

    private void IOSaving()
    {
        log("IO Processing");
        FileWriter writer = null;
        progress.addTotalSize(this.size);
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
            error("", e);
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
                    error("", e);
                }
            }
        }
    }

    private void NIOBufferedSaving()
    {
        log("NIO Processing");
        progress.addTotalSize(this.size);
        if(fileToSave != null)
        {
            Charset charset = Charset.forName("UTF-8");
            Path file = Paths.get(fileToSave.getAbsolutePath());
            try (BufferedWriter writer = Files.newBufferedWriter(file, charset))
            {
                StringBuffer buffer;
                while (nodes.hasNext())
                {
                    LogNode node = nodes.next();
                    buffer = new StringBuffer();
                    buffer.append(parser.parse(node));
                    buffer.append(NEW_LINE);
                    writer.write(buffer.toString());
                    progress.subtractSize(1L);
                }
            }
            catch (IOException e)
            {
                error("", e);
            }
        }
    }
    private void NIOUnbufferedSaving()
    {
        log("Unbuffered NIO Processing");
        progress.addTotalSize(this.size);
        if(fileToSave != null)
        {
            Path file = Paths.get(fileToSave.getAbsolutePath());
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE)))
            {
                StringBuffer buffer;
                while (nodes.hasNext())
                {
                    LogNode node = nodes.next();
                    buffer = new StringBuffer();
                    buffer.append(parser.parse(node));
                    buffer.append(NEW_LINE);
                    out.write(buffer.toString().getBytes());
                    progress.subtractSize(1L);
                }
            }
            catch (IOException e)
            {
                error("", e);
            }
        }
    }
    private void AlgorithmParsing()
    {
        log("Algorithm without parsing Processing");
        progress.addTotalSize(this.size);
        if(fileToSave != null)
        {
            StringBuffer buffer;
            while (nodes.hasNext())
            {
                LogNode node = nodes.next();
               /* buffer = new StringBuffer();
                buffer.append(parser.parse(node));
                buffer.append(NEW_LINE);
                buffer.toString();*/
                progress.subtractSize(1L);
            }
        }
    }
}
