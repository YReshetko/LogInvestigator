package com.my.home;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.ui.App;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Runner of UI application
 */
public class Runner extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        App app = (App) SpringBeanFactory.getInstance().getBean("Application");
        app.init(primaryStage);
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
