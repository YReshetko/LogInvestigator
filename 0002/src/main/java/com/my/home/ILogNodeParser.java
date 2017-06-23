package com.my.home;

import com.my.home.log.beans.LogNode;

import java.text.ParseException;

/**
 * Log node parser converts String to LogNode and LogNode back to String according log patterns
 */
public interface ILogNodeParser
{
    LogNode parse(String value) throws ParseException;
    String parse(LogNode node);
    boolean hasStamp(String logLine);
}
