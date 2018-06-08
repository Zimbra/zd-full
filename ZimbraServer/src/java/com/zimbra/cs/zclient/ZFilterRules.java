/*
 * 
 */
package com.zimbra.cs.zclient;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ZFilterRules implements ToZJSONObject {

    private List<ZFilterRule> mRules;

    public List<ZFilterRule> getRules() {
        return mRules;
    }

    public ZFilterRules(List<ZFilterRule> rules) {
        mRules = rules;
    }

    public ZFilterRules(ZFilterRules rules) {
        mRules = new ArrayList<ZFilterRule>();
        mRules.addAll(rules.getRules());
    }

    public ZFilterRules(Element e) throws ServiceException {
        mRules = new ArrayList<ZFilterRule>();
        for (Element ruleEl : e.listElements(MailConstants.E_FILTER_RULE)) {
            mRules.add(new ZFilterRule(ruleEl));
        }
    }

    public Element toElement(Element parent) {
        Element r = parent.addElement(MailConstants.E_FILTER_RULES);
        for (ZFilterRule rule : mRules) {
            rule.toElement(r);
        }
        return r;
    }

    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject jo = new ZJSONObject();
        jo.put("rules", mRules);
        return jo;
    }

    public String toString() {
        return String.format("[ZFilterRules]");
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }
}
