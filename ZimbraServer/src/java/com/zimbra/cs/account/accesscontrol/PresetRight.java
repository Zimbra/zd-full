/*
 * 
 */
package com.zimbra.cs.account.accesscontrol;

import java.util.HashSet;
import java.util.Set;

import com.zimbra.common.service.ServiceException;

public class PresetRight extends AdminRight {

    PresetRight(String name) {
        super(name, RightType.preset);
    }
    
    @Override
    public boolean isPresetRight() {
        return true;
    }
    
    
    // don't allow an account right to be granted on calendar resource and vice versa
    // it is only allowed for user rights
    // TODO: revisit, may be able to do with just removing account from setInheritedByTargetTypes for calresource
    //       and remove this very ugly check.   
    //       warning, need to examine all call sites of inherited by/from
    private boolean mutualExcludeAccountAndCalResource(TargetType targetType) {
        return ((mTargetType == TargetType.account && targetType == TargetType.calresource) ||
                (mTargetType == TargetType.calresource && targetType == TargetType.account));
    }
    
    @Override
    boolean grantableOnTargetType(TargetType targetType) {
        
        if (mutualExcludeAccountAndCalResource(targetType))
            return false;
        
        return targetType.isInheritedBy(mTargetType);
    }
    
    @Override
    protected Set<TargetType> getGrantableTargetTypes() {
        Set<TargetType> targetTypes = new HashSet<TargetType>();
        for (TargetType targetType : mTargetType.inheritFrom()) {
            if (!mutualExcludeAccountAndCalResource(targetType))
                targetTypes.add(targetType);
        }
        return targetTypes;
    }
    
    @Override
    boolean overlaps(Right other) throws ServiceException {
        if (other.isPresetRight())
            return this==other;
        else if (other.isAttrRight())
            return false;
        else if (other.isComboRight())
            return ((ComboRight)other).containsPresetRight(this);
        else
            throw ServiceException.FAILURE("internal error", null);
    }

}
