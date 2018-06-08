/*
 * 
 */
package com.zimbra.cs.index;

import java.util.List;

import com.zimbra.common.soap.Element;

public class SpellSuggestQueryInfo implements QueryInfo {
    
    static class Suggestion {
        public String mStr;
        public int mDocs;
        public int mEditDist;
    }
    
    private String mMisSpelled;
    private List<Suggestion> mSuggestions;
    
    SpellSuggestQueryInfo(String misSpelled, List<Suggestion> suggestions) {
        mMisSpelled = misSpelled;
        mSuggestions = suggestions;
    }

    public Element toXml(Element parent) {
        Element ms = parent.addElement("spell");
        ms.addAttribute("word", mMisSpelled);
        for (Suggestion s : mSuggestions) {
            Element elt = ms.addElement("sug");
            elt.addAttribute("dist", s.mEditDist);
            elt.addAttribute("numDocs", s.mDocs);
            elt.addAttribute("value", s.mStr);
        }
        return ms;
    }
    
    public String toString() {
        String toRet = "SUGGEST("+mMisSpelled+" [";
        for (Suggestion s : mSuggestions) {
            toRet = toRet +"("+ s.mStr+","+s.mEditDist+", "+s.mDocs+")   ";
        }
        return toRet + "]";
    }

}
