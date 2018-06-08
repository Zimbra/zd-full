/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class Provision extends Command {
    
    private static final String POLICY_TYPE_WAP = "MS-WAP-Provisioning-XML";
    
    public static enum RequestType {
        GET, ACK
    }

    public static String renewPolicy(SyncSettings syncSettings, boolean isDebugTraceOn) throws ServiceException {
        String newPolicyKey = null;
        try {
            Provision prov = new Provision(RequestType.GET, "0");
            prov.doCommand(syncSettings, "0", isDebugTraceOn);
            newPolicyKey = prov.getServerPolicyKey();
            if (prov.isRemoteWipe)
                throw ServiceException.FAILURE("Server requesting remote-wipe", null);
            prov = new Provision(RequestType.ACK, newPolicyKey);
            prov.doCommand(syncSettings, newPolicyKey, isDebugTraceOn);
            newPolicyKey = prov.getServerPolicyKey();
        } catch (CommandCallbackException x) {
            assert false;
        } catch (Exception x) {
            throw ServiceException.FAILURE("policy renew failed", x);
        }
        return newPolicyKey;
    }
    
    private RequestType requestType;
    private String clientPolicyKey;
    private int policyStatus;
    private String serverPolicyKey;
    private boolean isRemoteWipe;
    
    public Provision(RequestType requestType, String clientPolicyKey) {
        this.requestType = requestType;
        this.clientPolicyKey = clientPolicyKey;
    }

    String getServerPolicyKey() {
        return serverPolicyKey;
    }
    
    boolean isRemoteWipe() {
        assert requestType == RequestType.GET;
        return isRemoteWipe;
    }
    
    /* request to download */
    //  <Provision xmlns="Provision:">
    //    <Policies>
    //      <Policy>
    //        <PolicyType>MS-WAP-Provisioning-XML</PolicyType>
    //      </Policy>
    //    </Policies>
    //  </Provision>
    
    /* request to ack download */
    //  <Provision xmlns="Provision:">
    //    <Policies>
    //      <Policy>
    //        <PolicyType>MS-WAP-Provisioning-XML</PolicyType>
    //        <PolicyKey>1627090011</PolicyKey>
    //        <Status>1</Status>
    //      </Policy>
    //    </Policies>
    //  </Provision>
    
    @Override
    protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
        bs.openTag(NAMESPACE_PROVISION, PROVISION_PROVISION);
        bs.openTag(NAMESPACE_PROVISION, PROVISION_POLICIES);
        bs.openTag(NAMESPACE_PROVISION, PROVISION_POLICY);
        bs.textElement(NAMESPACE_PROVISION, PROVISION_POLICYTYPE, POLICY_TYPE_WAP);
        if (requestType == RequestType.ACK) {
            assert !clientPolicyKey.equals("0");
            bs.textElement(NAMESPACE_PROVISION, PROVISION_POLICYKEY, clientPolicyKey);
            bs.integerElement(NAMESPACE_PROVISION, PROVISION_STATUS, 1);
        }
        bs.closeTag(); //Policy
        bs.closeTag(); //Policies
        bs.closeTag(); //Provision
    }

    /* response to policy download */
    //  <Provision xmlns="Provision:">
    //    <Status>1</Status>
    //    <Policies>
    //      <Policy>
    //        <PolicyType>MS-WAP-Provisioning-XML</PolicyType>
    //        <Status>1</Status>
    //        <PolicyKey>1627090011</PolicyKey>
    //        <Data>
    //          <wap-provisioningdoc>
    //            <characteristic type="SecurityPolicy">
    //              <parm name="4131" value="0"/>
    //              <parm name="4133" value="0"/>
    //            </characteristic>
    //            <characteristic type="Registry">
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\AE\{50C13377-C66D-400C-889E-C316FC4AB374}">
    //                <parm name="AEFrequencyType" value="1"/>
    //                <parm name="AEFrequencyValue" value="15"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD">
    //                <parm name="DeviceWipeThreshold" value="4"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD">
    //                <parm name="CodewordFrequency" value="2"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\LAP\lap_pw">
    //                <parm name="MinimumPasswordLength" value="4"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\LAP\lap_pw">
    //                <parm name="PasswordComplexity" value="0"/>
    //              </characteristic>
    //            </characteristic>
    //          </wap-provisioningdoc>
    //        </Data>
    //      </Policy>
    //    </Policies>
    //  </Provision>
    
    /* response to policy ack */
    //  <Provision xmlns="Provision:">
    //    <Status>1</Status>
    //    <Policies>
    //      <Policy>
    //        <PolicyType>MS-WAP-Provisioning-XML</PolicyType>
    //        <Status>1</Status>
    //        <PolicyKey>2880546563</PolicyKey>
    //      </Policy>
    //    </Policies>
    //  </Provision>
    
    /* response to force remote wipe */
    //  <Provision xmlns="Provision:">
    //    <Status>1</Status>
    //    <Policies>
    //      <Policy>
    //        <PolicyType>MS-WAP-Provisioning-XML</PolicyType>
    //        <Status>1</Status>
    //        <PolicyKey>1627090011</PolicyKey>
    //        <Data>
    //          <wap-provisioningdoc>
    //            <characteristic type="SecurityPolicy">
    //              <parm name="4131" value="0"/>
    //              <parm name="4133" value="0"/>
    //            </characteristic>
    //            <characteristic type="Registry">
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\AE\{50C13377-C66D-400C-889E-C316FC4AB374}">
    //                <parm name="AEFrequencyType" value="1"/>
    //                <parm name="AEFrequencyValue" value="15"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD">
    //                <parm name="DeviceWipeThreshold" value="4"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD">
    //                <parm name="CodewordFrequency" value="2"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\LAP\lap_pw">
    //                <parm name="MinimumPasswordLength" value="4"/>
    //              </characteristic>
    //              <characteristic type="HKLM\Comm\Security\Policy\LASSD\LAP\lap_pw">
    //                <parm name="PasswordComplexity" value="0"/>
    //              </characteristic>
    //            </characteristic>
    //          </wap-provisioningdoc>
    //        </Data>
    //      </Policy>
    //    </Policies>
    //    <RemoteWipe/>
    //  </Provision>
    @Override
    public void parseResponse(BinaryParser bp)
            throws CommandCallbackException, BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_PROVISION, PROVISION_PROVISION);
        status = bp.nextIntegerElement(NAMESPACE_PROVISION, PROVISION_STATUS);
        if (status == 1) {
            bp.openTag(NAMESPACE_PROVISION, PROVISION_POLICIES);
            bp.openTag(NAMESPACE_PROVISION, PROVISION_POLICY);
            bp.nextTextElement(NAMESPACE_PROVISION, PROVISION_POLICYTYPE);
            policyStatus = bp.nextIntegerElement(NAMESPACE_PROVISION, PROVISION_STATUS);
            if (policyStatus == 1) {
                serverPolicyKey = bp.nextTextElement(NAMESPACE_PROVISION, PROVISION_POLICYKEY);
                if (requestType == RequestType.GET) {
                    bp.nextTag();
                    bp.require(START_TAG, NAMESPACE_PROVISION, PROVISION_DATA);
                    bp.skipElement();
                }
            }
            bp.closeTag(); //Policy
            bp.closeTag(); //Policies
            if (requestType == RequestType.GET) {
                if (bp.next() == START_TAG && bp.getName().equals(PROVISION_REMOTEWIPE)) {
                    isRemoteWipe = true;
                    bp.closeTag(); //RemoteWipe
                } else {
                    bp.require(END_TAG, NAMESPACE_PROVISION, PROVISION_PROVISION);
                    return;
                }
            }
        }
        bp.closeTag(); //Provision
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        //TODO:
        if (policyStatus != 1)
            throw new RuntimeException("policyStatus=" + policyStatus);
    }
}
