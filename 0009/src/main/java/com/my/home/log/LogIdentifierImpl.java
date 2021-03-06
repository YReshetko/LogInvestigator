package com.my.home.log;

import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.storage.ILogIdentifier;
import com.my.home.util.HashGenerator;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class LogIdentifierImpl implements ILogIdentifier
{
    private final static String DISC_PATTERN = "[a-zA-z]{1}:[\\\\/]{1,2}";
    private final static Pattern PATTERN = Pattern.compile(DISC_PATTERN);
    private final LogFilesDescriptor descriptor;
    private final String id;

    public LogIdentifierImpl(List<File> files, String directory)
    {
        if (files == null || files.size() == 0)
        {
            throw new IllegalArgumentException("You have to select at least one log file");
        }
        directory = directory.replaceFirst(PATTERN.pattern(), "");
        descriptor = new LogFilesDescriptor();
        descriptor.setDirectory(directory);
        descriptor.setName(files.get(0).getName());
        files.forEach(file -> descriptor.getFiles().add(file.getAbsolutePath()));
        this.id = HashGenerator.hash(descriptor.getFiles());
        descriptor.setId(this.id);
    }
    public LogIdentifierImpl(LogFilesDescriptor descriptor) {
        this.descriptor = descriptor;
        if (descriptor.getId() == null || descriptor.getId().toString().trim().isEmpty())
        {
            this.id = HashGenerator.hash(descriptor.getFiles());
            descriptor.setId(this.id);
        }
        else
        {
            this.id = descriptor.getId();
        }
    }

    @Override
    public String getKey() {
        return this.id;
    }

    @Override
    public LogFilesDescriptor getLogDescriptor() {
        return this.descriptor;
    }
}
