package com.my.home.ui.tree;

import com.my.home.storage.ILogIdentifier;

/**
 *
 */
public interface ILogTreeListener
{
    void dispatch(ILogIdentifier identifier);
}
