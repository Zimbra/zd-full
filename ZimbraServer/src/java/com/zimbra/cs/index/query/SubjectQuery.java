/*
 * 
 */
package com.zimbra.cs.index.query;

import org.apache.lucene.analysis.Analyzer;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.DBQueryOperation;
import com.zimbra.cs.index.LuceneFields;
import com.zimbra.cs.index.QueryOperation;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query by subject.
 *
 * @author tim
 * @author ysasaki
 */
public final class SubjectQuery extends Query {
    private String mStr;
    private boolean mLt;
    private boolean mEq;

    @Override
    public QueryOperation getQueryOperation(boolean bool) {
        DBQueryOperation op = new DBQueryOperation();
        if (mLt) {
            op.addRelativeSubject(null, false, mStr, mEq, evalBool(bool));
        } else {
            op.addRelativeSubject(mStr, mEq, null, false, evalBool(bool));
        }
        return op;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append("SUBJECT");
        out.append(mLt ? '<' : '>');
        if (mEq) {
            out.append('=');
        }
        out.append(mStr);
    }

    /**
     * Don't call directly -- use SubjectQuery.create()
     *
     * This is only invoked for subject queries that start with {@code <} or
     * {@code >}, otherwise we just use the normal TextQuery class.
     */
    private SubjectQuery(String text) {
        mLt = (text.charAt(0) == '<');
        mEq = false;
        mStr = text.substring(1);

        if (mStr.charAt(0) == '=') {
            mEq = true;
            mStr= mStr.substring(1);
        }

        // bug: 27976 -- we have to allow >"" for cursors to work as expected
        //if (mStr.length() == 0)
        //    throw MailServiceException.PARSE_ERROR("Invalid subject string: "+text, null);
    }

    public static Query create(Mailbox mbox, Analyzer analyzer, String text)
        throws ServiceException {
        if (text.length() > 1 && (text.startsWith("<") || text.startsWith(">"))) {
            // real subject query!
            return new SubjectQuery(text);
        } else {
            return new TextQuery(mbox, analyzer, LuceneFields.L_H_SUBJECT, text);
        }
    }
}
