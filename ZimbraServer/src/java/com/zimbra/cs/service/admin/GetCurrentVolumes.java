/*
 * 
 */

package com.zimbra.cs.service.admin;

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

public class GetCurrentVolumes extends AdminDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);

        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(lc, context, localServer, Admin.R_manageVolume);
        
        Element response = lc.createElement(AdminConstants.GET_CURRENT_VOLUMES_RESPONSE);

        Volume msgVol = Volume.getCurrentMessageVolume();
        response.addElement(AdminConstants.E_VOLUME)
                .addAttribute(AdminConstants.A_VOLUME_TYPE, Volume.TYPE_MESSAGE)
                .addAttribute(AdminConstants.A_ID, msgVol.getId());

        Volume secondaryMsgVol = Volume.getCurrentSecondaryMessageVolume();
        if (secondaryMsgVol != null)
            response.addElement(AdminConstants.E_VOLUME)
                    .addAttribute(AdminConstants.A_VOLUME_TYPE, Volume.TYPE_MESSAGE_SECONDARY)
                    .addAttribute(AdminConstants.A_ID, secondaryMsgVol.getId());

        Volume indexVol = Volume.getCurrentIndexVolume();
        response.addElement(AdminConstants.E_VOLUME)
                .addAttribute(AdminConstants.A_VOLUME_TYPE, Volume.TYPE_INDEX)
                .addAttribute(AdminConstants.A_ID, indexVol.getId());

        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_manageVolume);
    }
    
}
