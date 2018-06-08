/*
 * 
 */
package com.zimbra.cs.account.callback;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;

public class WorkingHours extends AttributeCallback {

    // Value must be a comma-separated string whose parts are colon-separated strings.
    // Each comma-separated part specifies the working hours of a day of the week.
    // Each day of the week must be specified exactly once.
    // 
    @Override
    public void preModify(Map context, String attrName, Object attrValue, Map attrsToModify, Entry entry, boolean isCreate)
    throws ServiceException {
        if (attrValue == null) return;  // Allow unsetting.
        if (!(attrValue instanceof String))
            throw ServiceException.INVALID_REQUEST(attrValue + " is a single-valued string", null);
        String value = (String) attrValue;
        if (value.length() == 0) return;  // Allow unsetting.
        com.zimbra.cs.fb.WorkingHours.validateWorkingHoursPref(value);
    }

    @Override
    public void postModify(Map context, String attrName, Entry entry, boolean isCreate) {
    }
}
