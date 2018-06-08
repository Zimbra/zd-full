/*
 * 
 */
package com.zimbra.cs.index.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import com.zimbra.cs.index.LuceneQueryOperation;
import com.zimbra.cs.index.QueryOperation;

/**
 * Query by email domain.
 *
 * @author tim
 * @author ysasaki
 */
public final class DomainQuery extends Query {
    private final String field;
    private final String term;

    public DomainQuery(String field, String term) {
        this.field = field;
        this.term = term;
    }

    @Override
    public QueryOperation getQueryOperation(boolean bool) {
        LuceneQueryOperation op = new LuceneQueryOperation();
        op.addClause(toQueryString(field, term),
                new TermQuery(new Term(field, term)), evalBool(bool));
        return op;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append("DOMAIN,");
        out.append(term);
    }
}
