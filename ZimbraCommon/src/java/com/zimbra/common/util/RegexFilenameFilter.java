/*
 * 
 */
package com.zimbra.common.util;

import java.io.File;
import java.io.FilenameFilter;

public class RegexFilenameFilter implements FilenameFilter {

    protected String regex;
    
    public RegexFilenameFilter(String regex) {
        super();
        this.regex = regex;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.matches(regex);
    }
}