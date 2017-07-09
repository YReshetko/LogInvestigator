package com.my.home.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Utils to work with file system
 */
public class FileChooserUtil
{
    public static File initialDir = null;
    public static File getFile(Stage parent, String label, String... extensions)
    {
        File out = getChooser(label, extensions).showOpenDialog(parent);
        if(out != null)
        {
            initialDir = out.getParentFile();
        }
        return out;
    }

    public static File saveFile(Stage parent, String label, String initFileName, String... extensions)
    {
        FileChooser chooser = getChooser(label, extensions);
        chooser.setInitialFileName(initFileName);
        File out = chooser.showSaveDialog(parent);
        if(out != null)
        {
            initialDir = out.getParentFile();
        }
        return out;
    }

    public static List<File> getFiles(Stage parent, String label, String... extensions)
    {
        List<File> out = getChooser(label, extensions).showOpenMultipleDialog(parent);
        if(out != null && out.size()>0)
        {
            initialDir = out.get(0).getParentFile();
        }
        return out;
    }

    public static File showDirChooser(Stage parent, String label)
    {
        DirectoryChooser chooser = new DirectoryChooser();
        if(initialDir != null)
        {
            chooser.setInitialDirectory(initialDir);
        }
        chooser.setTitle(label);
        File out = chooser.showDialog(parent);
        if(out != null)
        {
            initialDir = out;
        }
        return out;
    }

    private static FileChooser getChooser(String label, String... extensions)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(label, extensions));
        if(initialDir != null)
        {
            fileChooser.setInitialDirectory(initialDir);
        }
        return fileChooser;
    }
}
