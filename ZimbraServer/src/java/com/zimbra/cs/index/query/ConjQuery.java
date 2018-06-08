/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.cs.index.QueryOperation;

/**
 * Special query that combine queries.
 *
 * @author tim
 * @author ysasaki
 */
public final class ConjQuery extends Query {

    public enum Conjunction {
        AND("&&"), OR("||");

        private final String symbol;

        private Conjunction(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    private final Conjunction conjunction;

    public ConjQuery(Conjunction conj) {
        conjunction = conj;
    }

    public Conjunction getConjunction() {
        return conjunction;
    }

    @Override
    public StringBuilder toString(StringBuilder out) {
        out.append(' ');
        dump(out);
        return out.append(' ');
    }

    @Override
    public void dump(StringBuilder out) {
        out.append(conjunction);
    }

    @Override
    public QueryOperation getQueryOperation(boolean truth) {
        assert(false);
        return null;
    }

}
