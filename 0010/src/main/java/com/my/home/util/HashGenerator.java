package com.my.home.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HashGenerator
{
    public static String hash(String value)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            update(md, value);
            return getResult(md);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Cant generate hash", e);
        }
    }
    public static String hash(List<String> values)
    {
        try
        {
            Collections.sort(values);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            for (String value : values)
            {
                update(md, value);
            }
            return getResult(md);
        }
            catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cant generate hash for list of string", e);
        }
    }

    private static void update(MessageDigest md, String value)
    {
        byte[] bytes = value.getBytes();
        md.update(bytes);
    }
    private static String getResult(MessageDigest md)
    {
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String result = bigInt.toString(16);
        return result;
    }
}
