/*
 * 
 */
package com.zimbra.cs.extension;

import com.zimbra.cs.extension.ZimbraExtension;

/**
 * Simple extension for testing.
 *
 * @author ysasaki
 */
public class SimpleExtension implements ZimbraExtension {
    private boolean initialized = false;
    private boolean destroyed = false;

    public String getName() {
        return "simple";
    }

    public void init() {
        initialized = true;
    }

    public void destroy() {
        destroyed = true;
    }

    boolean isInitialized() {
        return initialized;
    }

    boolean isDestroyed() {
        return destroyed;
    }

}
