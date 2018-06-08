/*
 * 
 */
package com.zimbra.cs.index.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.LuceneFields;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * A simpler way of expressing (to:FOO or from:FOO or cc:FOO).
 *
 * @author tim
 * @author ysasaki
 */
public final class AddrQuery extends SubQuery {

    public enum Address {
        FROM, TO, CC
    }

    private AddrQuery(List<Query> clauses) {
        super(clauses);
    }

    public static AddrQuery create(Mailbox mbox, Analyzer analyzer,
            Set<Address> addrs, String text) throws ServiceException {
        List<Query> clauses = new ArrayList<Query>();
        boolean atFirst = true;

        if (addrs.contains(Address.FROM)) {
            clauses.add(new TextQuery(mbox, analyzer, LuceneFields.L_H_FROM, text));
            atFirst = false;
        }

        if (addrs.contains(Address.TO)) {
            if (atFirst) {
                atFirst = false;
            } else {
                clauses.add(new ConjQuery(ConjQuery.Conjunction.OR));
            }
            clauses.add(new TextQuery(mbox, analyzer, LuceneFields.L_H_TO, text));
        }

        if (addrs.contains(Address.CC)) {
            if (atFirst) {
                atFirst = false;
            } else {
                clauses.add(new ConjQuery(ConjQuery.Conjunction.OR));
            }
            clauses.add(new TextQuery(mbox, analyzer, LuceneFields.L_H_CC, text));
        }

        return new AddrQuery(clauses);
    }
}
