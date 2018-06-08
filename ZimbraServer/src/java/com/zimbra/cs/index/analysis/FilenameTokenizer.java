/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

/**
 * Split by comma, space, CR, LF, dot.
 *
 * @author tim
 * @author ysasaki
 */
public final class FilenameTokenizer extends CharTokenizer {

    public FilenameTokenizer(Reader reader) {
        super(reader);
    }

    @Override
    protected boolean isTokenChar(char c) {
        switch (c) {
            case ',':
            case ' ':
            case '\r':
            case '\n':
            case '.':
                return false;
            default:
                return true;
        }
    }

    @Override
    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }

}
