/*
 * 
 */
package com.zimbra.cs.index;

public class BrowseTerm {
    public final String term;
    public final int freq;
    
    public BrowseTerm(String term, int freq) {
        this.term = term;
        this.freq = freq;
    }

    @Override
    public int hashCode() {
        return term.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BrowseTerm other = (BrowseTerm) obj;
        if (term == null) {
            if (other.term != null)
                return false;
        } else if (!term.equals(other.term))
            return false;
        return true;
    }
    
    
}