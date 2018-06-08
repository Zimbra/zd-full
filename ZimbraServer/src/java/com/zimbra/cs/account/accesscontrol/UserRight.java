/*
 * 
 */
package com.zimbra.cs.account.accesscontrol;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.accesscontrol.generated.UserRights;

public class UserRight extends Right {
    
    static void init(RightManager rm) throws ServiceException {
        UserRights.init(rm);
    }
    
    UserRight(String name) {
        super(name, RightType.preset);
    }
    
    @Override
    public boolean isUserRight() {
        return true;
    }
    
    @Override
    public boolean isPresetRight() {
        return true;
    }

    @Override
    boolean executableOnTargetType(TargetType targetType) {
        // special treatment for user right:
        // if a right is executable on account, it is executable on calendar resource
        if (mTargetType == TargetType.account)
            return (targetType == TargetType.account || targetType == TargetType.calresource);
        else
            return super.executableOnTargetType(targetType);    
    }
    
    @Override
    boolean allowSubDomainModifier() {
        return false;
    }
    
    /*
    String dump(StringBuilder sb) {
        // nothing in user right to dump
        return super.dump(sb);
    }
    */
    
    @Override
    boolean overlaps(Right other) throws ServiceException {
        return this==other; 
        // no need to check is other is a combo right, because
        // combo right can only contain admin rights.
    }

}
