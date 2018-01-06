package com.my.home.task;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 *
 */
public class AfterDownloadTask extends AbstractAppTask<File>
{

    public AfterDownloadTask(Future<File> future) {
        super(future);
    }

    @Override
    protected void executeResult(File result) {
        if (result != null && result.isFile() && result.exists())
        {
            try
            {
                Desktop.getDesktop().open(result);
            }
            catch (IOException e)
            {
                error("Can't open file " + result.getAbsolutePath(), e);
            }
        }
    }
}
