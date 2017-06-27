package com.my.home.storage.mongo.wrapper;

/**
 *
 */
public class DBRequestTemplates
{
    public static final String IDENTIFIER_FIELD_NAME = "identifier";
    public static final String VALUE_FIELD_NAME = "value";
    public static final String TO_SAVE = "{\""+IDENTIFIER_FIELD_NAME+"\" : \"%s\", "+VALUE_FIELD_NAME+" : %s}";
}
