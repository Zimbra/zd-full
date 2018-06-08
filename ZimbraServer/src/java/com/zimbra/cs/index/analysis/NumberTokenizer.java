/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * Numbers separated by ' ' or '\t'.
 *
 * @author tim
 * @author ysasaki
 */
public final class NumberTokenizer extends Tokenizer {

    private int mEndPos = 0;
    private TermAttribute termAttr = addAttribute(TermAttribute.class);
    private OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);

    public NumberTokenizer(Reader reader) {
        super(reader);
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();

        int startPos = mEndPos;
        StringBuilder buf = new StringBuilder(10);

        while (true) {
            int c = input.read();
            mEndPos++;
            switch (c) {
                case -1:
                    if (buf.length() == 0) {
                        return false;
                    }
                    // no break!
                case ' ':
                case '\t':
                    if (buf.length() != 0) {
                        termAttr.setTermBuffer(buf.toString());
                        offsetAttr.setOffset(startPos, mEndPos - 1);
                        return true;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buf.append((char) c);
                    break;
                default:
                    // ignore char
            }
        }
    }

}
