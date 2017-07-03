package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.log.LogNodeParser;
import com.my.home.parser.ParserManager;
import com.my.home.ui.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 *
 */
public class ParserOptionsController implements IUIController
{
    @FXML
    private TextField commonStamp;
    @FXML
    private TextField commonDTStamp;
    @FXML
    private TextField datePattern;
    @FXML
    private TextField timePattern;
    @FXML
    private TextField millisPattern;
    @FXML
    private TextField logLvlPattern;
    @FXML
    private TextField threadPattern;
    @FXML
    private TextField classPattern;
    @FXML
    private TextField dateFormat;
    @FXML
    private TextField timeFormat;

    private App app;
    public ParserOptionsController()
    {
        app = (App) SpringBeanFactory.getInstance().getBean("Application");
    }

    @Override
    public void init()
    {
        showPatterns();
    }

    private void showPatterns()
    {
        ParserManager manager = app.getParserManager();
        LogNodeParser parser = manager.getParser("default");
        commonStamp.setText(parser.getCommonStampPattern());
        commonDTStamp.setText(parser.getCommonDataTimePattern());
        datePattern.setText(parser.getDatePattern());
        timePattern.setText(parser.getTimePattern());
        millisPattern.setText(parser.getMillisecondsPattern());
        logLvlPattern.setText(parser.getLogLevelPattern());
        threadPattern.setText(parser.getThreadPatten());
        classPattern.setText(parser.getClassPattern());
        dateFormat.setText(parser.getDateFormat());
        timeFormat.setText(parser.getTimeFormat());
    }
    private void savePatterns()
    {

    }
}
