/*
 * 
 */

package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class SetCurrentVolume extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(zsc, context, localServer, Admin.R_manageVolume);
        
        short volType = (short) request.getAttributeLong(AdminConstants.A_VOLUME_TYPE);
        long idLong = request.getAttributeLong(AdminConstants.A_ID, Volume.ID_NONE);
        Volume.validateID(idLong, true);  // avoid Java truncation
        short id = (short) idLong;
        Volume.setCurrentVolume(volType, id);

        Element response = zsc.createElement(AdminConstants.SET_CURRENT_VOLUME_RESPONSE);
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageVolume);
    }
}
