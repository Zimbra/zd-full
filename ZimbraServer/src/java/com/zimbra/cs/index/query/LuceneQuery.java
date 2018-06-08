/*
 * 
 */
package com.zimbra.cs.index.query;

import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import com.zimbra.cs.index.LuceneQueryOperation;
import com.zimbra.cs.index.QueryOperation;

/**
 * Query by Lucene field.
 *
 * @author tim
 * @author ysasaki
 */
abstract class LuceneQuery extends Query {
    private final String luceneField;
    private final String queryField;
    private final String term;

    static String lookup(Map<String, String> map, String key) {
        String toRet = map.get(key);
        if (toRet == null) {
            return key;
        } else {
            return toRet;
        }
    }

    LuceneQuery(String queryField, String luceneField, String term) {
        this.queryField = queryField;
        this.luceneField = luceneField;
        this.term = term;
    }

    @Override
    public QueryOperation getQueryOperation(boolean bool) {
        LuceneQueryOperation op = new LuceneQueryOperation();
        op.addClause(queryField + term,
                new TermQuery(new Term(luceneField, term)), evalBool(bool));
        return op;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append(luceneField);
        out.append(',');
        out.append(term);
    }

}
