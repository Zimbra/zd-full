/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

/**
 * Tokenizer for email addresses.
 */
public final class AddrCharTokenizer extends CharTokenizer {

    public AddrCharTokenizer(Reader reader) {
        super(reader);
    }

    @Override
    protected boolean isTokenChar(char ch) {
        switch (ch) {
            case ' ':
            case '\u3000': // fullwidth space
            case '\r':
            case '\n':
            case '<':
            case '>':
            case '\"':
            case ',':
            case '\'':
            case '(':
            case ')':
            case '[':
            case ']':
                return false;
        }
        return true;
    }

    @Override
    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }

}
