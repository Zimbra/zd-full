/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

/**
 * Comma-separated values, typically for content type list.
 *
 * @author tim
 * @author ysasaki
 */
public final class CSVTokenizer extends CharTokenizer {

    public CSVTokenizer(Reader in) {
        super(in);
    }

    @Override
    protected boolean isTokenChar(char c) {
        return c != ',';
    }

    @Override
    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }

}
