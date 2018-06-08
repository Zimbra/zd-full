/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.cs.zclient.ZSelectiveCallRejection;

import java.util.ArrayList;
import java.util.List;

public class ZSelectiveCallRejectionBean extends ZCallFeatureBean {

    public ZSelectiveCallRejectionBean(ZSelectiveCallRejection feature) {
        super(feature);
    }

    public List<String> getRejectFrom() {
        List<String> data = getFeature().getRejectFrom();
        List<String> result = new ArrayList<String>(data.size());
        for (String name : data) {
            result.add(ZPhone.getDisplay(name));
        }
        return result;
    }

    public void setRejectFrom(List<String> list) {
        List<String> names = new ArrayList<String>(list.size());
        for (String display : list) {
            names.add(ZPhone.getNonFullName(display));
        }
        getFeature().setRejectFrom(names);
    }

    protected ZSelectiveCallRejection getFeature() {
        return (ZSelectiveCallRejection) super.getFeature(); 
    }
}
