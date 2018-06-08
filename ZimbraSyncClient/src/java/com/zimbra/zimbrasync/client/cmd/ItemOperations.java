/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class ItemOperations extends Command {

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }

    // <ItemOperations xmlns="ItemOperations:" xmlns:A="AirSync:" xmlns:B="POOMMAIL:" xmlns:C="AirSyncBase:">
    //   <Status>1</Status>
    //   <Response>
    //     <Fetch>
    //       <Status>1</Status>
    //       <A:CollectionId>5</A:CollectionId>
    //       <A:ServerId>5:6</A:ServerId>
    //       <A:Class>Email</A:Class>
    //       <Properties>
    //         <B:To>"smukhopadhyay@zimbra.com" &lt;smukhopadhyay@zimbra.com&gt;</B:To>
    //         <B:From>"SUDIPTO MUKHOPADHYAY" &lt;sudipto674@yahoo.com&gt;</B:From>
    //         <B:Subject>Fw: LinkedIn Network Updates, 8/03/2010</B:Subject>
    //         <B:DateReceived>2010-08-06T23:14:20.847Z</B:DateReceived>
    //         <B:DisplayTo>smukhopadhyay@zimbra.com</B:DisplayTo>
    //         <B:ThreadTopic>LinkedIn Network Updates, 8/03/2010</B:ThreadTopic>
    //         <B:Importance>1</B:Importance><B:Read>1</B:Read>
    //         <C:Body>
    //           <C:Type>2</C:Type>
    //           <C:EstimatedDataSize>13942</C:EstimatedDataSize>
    //           <Part>1</Part>
    //         </C:Body>
    //         <B:MessageClass>IPM.Note</B:MessageClass>
    //         <B:InternetCPID>65001</B:InternetCPID>
    //         <B:Flag/>
    //         <B:ContentClass>urn:content-classes:message</B:ContentClass>
    //         <C:NativeBodyType>2</C:NativeBodyType>
    //       </Properties>
    //     </Fetch>
    //   </Response>
    // </ItemOperations>
    
    // <ItemOperations xmlns="ItemOperations">
    //   <Status>1</Status>
    //   <Response>
    //     <Fetch>
    //        <Status>1</Status>
    //        <AirSyncBase:FileReference>402/4</AirSyncBase:FileReference>
    //        <Properties>
    //            <AirSyncBase:ContentType>image/jpeg; name=103-0324_IMG_2.jpg</AirSyncBase:ContentType>
    //            <Part>1</Part>
    //        </Properties>
    //     </Fetch>
    //   </Response>
    // </ItemOperations>

    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_ITEMOPERATIONS);
        int status = bp.nextIntegerElement(NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_STATUS);
        if (status == 1) {
            bp.openTag(NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_RESPONSE);
            bp.openTag(NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_FETCH);
            int sts = bp.nextIntegerElement(NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_STATUS);
            if (sts == 1) {
                while (bp.next() == START_TAG) {
                    if (AIRSYNCBASE_FILEREFERENCE.equals(bp.getName()) && NAMESPACE_AIRSYNCBASE.equals(bp.getNamespace()))
                        bp.nextText(); // FileReference
                    else if (AIRSYNC_COLLECTIONID.equals(bp.getName()) && NAMESPACE_AIRSYNC.equals(bp.getNamespace()))
                        bp.nextText(); // A:CollectionId
                    else if (AIRSYNC_SERVERID.equals(bp.getName()) && NAMESPACE_AIRSYNC.equals(bp.getNamespace()))
                        bp.nextText(); // A:ServerId
                    else if (AIRSYNC_CLASS.equals(bp.getName()) && NAMESPACE_AIRSYNC.equals(bp.getNamespace()))
                        bp.nextText(); // A:Class
                    else if (ITEMOPERATIONS_PROPERTIES.equals(bp.getName()) && NAMESPACE_ITEMOPERATIONS.equals(bp.getNamespace()))
                        bp.skipElement();
                    else
                        bp.skipUnknownElement();
                }
            }
            bp.require(END_TAG, NAMESPACE_ITEMOPERATIONS, ITEMOPERATIONS_FETCH); // End of Fetch
            bp.closeTag(); // Response
        }
        bp.closeTag(); // ItemOperations
    }

}
