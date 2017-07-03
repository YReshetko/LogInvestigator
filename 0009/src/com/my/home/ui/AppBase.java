package com.my.home.ui;

/**
 *
 */
public class AppBase
{
    /**
     * Windows
     */
    protected String mainWindowFX;
    protected String optionWindowFX;

    /**
     * Titles
     */
    protected String mainWindowTitle;
    protected String optionWindowTitle;

    /**
     * Sizes
     */
    protected int mainWindowInitWidth;
    protected int mainWindowInitHeight;

    protected int optionWindowInitWidth;
    protected int optionWindowInitHeight;

    public String getOptionWindowFX() {
        return optionWindowFX;
    }

    public void setOptionWindowFX(String optionWindowFX) {
        this.optionWindowFX = optionWindowFX;
    }

    public String getOptionWindowTitle() {
        return optionWindowTitle;
    }

    public void setOptionWindowTitle(String optionWindowTitle) {
        this.optionWindowTitle = optionWindowTitle;
    }

    public String getMainWindowFX() {
        return mainWindowFX;
    }

    public void setMainWindowFX(String mainWindowFX) {
        this.mainWindowFX = mainWindowFX;
    }

    public String getMainWindowTitle() {
        return mainWindowTitle;
    }

    public void setMainWindowTitle(String mainWindowTitle) {
        this.mainWindowTitle = mainWindowTitle;
    }

    public int getMainWindowInitWidth() {
        return mainWindowInitWidth;
    }

    public void setMainWindowInitWidth(int mainWindowInitWidth) {
        this.mainWindowInitWidth = mainWindowInitWidth;
    }

    public int getMainWindowInitHeight() {
        return mainWindowInitHeight;
    }

    public void setMainWindowInitHeight(int mainWindowInitHeight) {
        this.mainWindowInitHeight = mainWindowInitHeight;
    }

    public int getOptionWindowInitWidth() {
        return optionWindowInitWidth;
    }

    public void setOptionWindowInitWidth(int optionWindowInitWidth) {
        this.optionWindowInitWidth = optionWindowInitWidth;
    }

    public int getOptionWindowInitHeight() {
        return optionWindowInitHeight;
    }

    public void setOptionWindowInitHeight(int optionWindowInitHeight) {
        this.optionWindowInitHeight = optionWindowInitHeight;
    }
}
