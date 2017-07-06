package com.my.home.processor;

/**
 *
 */
public interface ILogProgress
{
    void addTotalSize(long size);
    void setTotalSize(long size);
    void subtractSize(long size);
    double getProgress();
}
