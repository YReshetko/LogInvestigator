package com.my.home.ui.windows;

/**
 *
 */
public class WindowDescriptor
{
    /**
     * Windows
     */
    private String fxmlDoc;

    /**
     * Titles
     */
    private String title;

    /**
     * Sizes
     */
    private int width;
    private int height;

    public String getFxmlDoc() {
        return fxmlDoc;
    }

    public void setFxmlDoc(String fxmlDoc) {
        this.fxmlDoc = fxmlDoc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
