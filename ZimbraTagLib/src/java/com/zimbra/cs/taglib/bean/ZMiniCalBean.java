/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class ZMiniCalBean {

    private Map<String,Boolean> mDays;

    public ZMiniCalBean(Set<String> days) {
        mDays = new HashMap<String,Boolean>();
        for (String day : days) {
            mDays.put(day, true);
        }
    }

    public Map<String,Boolean> getDays() {
        return mDays;
    }
}