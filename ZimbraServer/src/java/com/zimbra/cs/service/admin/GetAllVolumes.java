/*
 * 
 */

package com.zimbra.cs.service.admin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.common.soap.AdminConstants;

public class GetAllVolumes extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(zsc, context, localServer, Admin.R_manageVolume);
        
        List vols = Volume.getAll();
        Element response = zsc.createElement(AdminConstants.GET_ALL_VOLUMES_RESPONSE);
        for (Iterator it = vols.iterator(); it.hasNext(); )
            GetVolume.addVolumeElement(response, (Volume) it.next());
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageVolume);
    }
}
