/*
 * 
 */
package com.zimbra.cs.index;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for query operations that combine sets of sub-operations
 * (e.g. Intersections or Unions)
 */
abstract class CombiningQueryOperation extends QueryOperation {

    protected List<QueryOperation> mQueryOperations =
        new ArrayList<QueryOperation>();

    int getNumSubOps() {
        return mQueryOperations.size();
    }

}
