/*
 * 
 */

package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.soap.ZimbraSoapContext;

public class DeleteVolume extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);

        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(lc, context, localServer, Admin.R_manageVolume);
        
        long idLong = request.getAttributeLong(AdminConstants.A_ID);
        Volume.validateID(idLong);  // avoid Java truncation
        short id = (short) idLong;
        // make sure the volume exists before doing anything heavyweight...
        Volume.getById(id);
        Volume.delete(id);

        Element response = lc.createElement(AdminConstants.DELETE_VOLUME_RESPONSE);
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageVolume);
    }
}
