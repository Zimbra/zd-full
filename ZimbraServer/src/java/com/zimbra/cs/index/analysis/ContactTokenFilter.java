/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * Swallow dots, but include dots in a token only when it is not the only char
 * in the token.
 */
public final class ContactTokenFilter extends TokenFilter {
    private TermAttribute termAttr = addAttribute(TermAttribute.class);

    public ContactTokenFilter(AddrCharTokenizer input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            if (termAttr.termLength() == 1 && termAttr.termBuffer()[0] == '.') {
                continue; // swallow dot
            } else {
                return true;
            }
        }
        return false;
    }

}
