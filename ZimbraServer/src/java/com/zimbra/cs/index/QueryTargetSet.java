/*
 * 
 */
package com.zimbra.cs.index;

import java.util.HashSet;

class QueryTargetSet extends HashSet<QueryTarget> 
{
	public QueryTargetSet() {
	}
	
	public QueryTargetSet(int size) {
		super(size);
	}
	
	/**
	 * Used in the optimize() pathway, count the number of explicit
	 * QueryTarget's (ie don't count "unspecified") 
	 * @return
	 */
	int countExplicitTargets() {
		int toRet = 0;
		for (QueryTarget q : this) {
			if (q != QueryTarget.UNSPECIFIED)
				toRet++;
		}
		return toRet;		
	}
	
	boolean isSubset(QueryTargetSet other) {
		for (QueryTarget t : this) {
			if (!other.contains(t))
				return false;
		}
		return true;
	}

	boolean hasExternalTargets() {
		for (QueryTarget t : this) {
			if (t != QueryTarget.UNSPECIFIED && t != QueryTarget.LOCAL)
				return true;
		}
		return false;
	}
	
}
