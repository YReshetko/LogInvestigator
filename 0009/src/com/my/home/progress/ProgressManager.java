package com.my.home.progress;

import com.my.home.processor.ILogProgress;
import javafx.scene.control.ProgressBar;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class ProgressManager implements ILogProgress
{

    private final AtomicLong totalSize;
    private final AtomicLong currentSize;
    private final ProgressBar progressBar;
    public ProgressManager(ProgressBar progressBar)
    {
        totalSize = new AtomicLong(0);
        currentSize = new AtomicLong(0);
        this.progressBar = progressBar;
    }
    @Override
    public void addTotalSize(long size) {
        totalSize.addAndGet(size);
        calculateProgress();
    }

    @Override
    public synchronized void setTotalSize(long size) {
        totalSize.set(size);
        currentSize.set(0);
        calculateProgress();
    }

    @Override
    public void subtractSize(long size) {
        currentSize.addAndGet(size);
        calculateProgress();
    }

    private double calculateProgress()
    {
        double currentProgress = 0;
        Double bigTotalSize = new Double(String.valueOf(totalSize.get()));
        Double bigCurrentSize = new Double(String.valueOf(currentSize.get()));
        if (bigTotalSize>0)
        {
            Double result = bigCurrentSize/bigTotalSize;
            currentProgress = result;
        }
        else
        {
            currentProgress = 0;
        }
        if(bigTotalSize.equals(bigCurrentSize))
        {
            currentProgress = 0;
        }
        progressBar.setProgress(currentProgress);

        return currentProgress;
    }
    @Override
    public double getProgress() {
        return calculateProgress();
    }
}
