/*
 * 
 */
package com.zimbra.cs.index.query;

import java.util.List;

import com.zimbra.cs.index.QueryOperation;

/**
 * Special query that wraps sub queries.
 *
 * @author tim
 * @author ysasaki
 */
public class SubQuery extends Query {

    private final List<Query> clauses;

    public SubQuery(List<Query> clauses) {
        this.clauses = clauses;
    }

    public List<Query> getSubClauses() {
        return clauses;
    }

    @Override
    public QueryOperation getQueryOperation(boolean truth) {
        assert(false);
        return null;
    }

    @Override
    public StringBuilder toString(StringBuilder out) {
        out.append(getModifier());
        out.append('(');
        dump(out);
        return out.append(')');
    }

    @Override
    public void dump(StringBuilder out) {
        for (Query sub : clauses) {
            sub.toString(out);
        }
    }

}
