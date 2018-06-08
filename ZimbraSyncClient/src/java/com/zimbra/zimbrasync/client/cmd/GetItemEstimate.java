/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class GetItemEstimate extends Command {

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }

    //  <GetItemEstimate xmlns="GetItemEstimate:">
    //    <Response>
    //      <Status>1</Status>
    //      <Collection>
    //        <Class>Contacts</Class>
    //        <CollectionId>2</CollectionId>
    //        <Estimate>0</Estimate>
    //      </Collection>
    //    </Response>
    //    <Response>
    //      <Status>1</Status>
    //      <Collection>
    //        <Class>Calendar</Class>
    //        <CollectionId>1</CollectionId>
    //        <Estimate>0</Estimate><
    //      </Collection>
    //    </Response>
    //    <Response>
    //      <Status>1</Status>
    //      <Collection>
    //        <Class>Email</Class>
    //        <CollectionId>5</CollectionId>
    //        <Estimate>2</Estimate>
    //      </Collection>
    //    </Response>
    //    <Response>
    //      <Status>1</Status>
    //      <Collection>
    //        <Class>Tasks</Class>
    //        <CollectionId>11</CollectionId>
    //        <Estimate>0</Estimate>
    //      </Collection>
    //    </Response>
    //  </GetItemEstimate>
    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_GETITEMESTIMATE, GETITEMESTIMATE_GETITEMESTIMATE);
        while (bp.next() == START_TAG && NAMESPACE_GETITEMESTIMATE.equals(bp.getNamespace())) {
            if (GETITEMESTIMATE_RESPONSE.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_GETITEMESTIMATE.equals(bp.getNamespace())) {
                    if (GETITEMESTIMATE_STATUS.equals(bp.getName()))
                        bp.nextIntegerContent();
                    else if (GETITEMESTIMATE_COLLECTION.equals(bp.getName())) {
                        while (bp.next() == START_TAG && NAMESPACE_GETITEMESTIMATE.equals(bp.getNamespace())) {
                            if (GETITEMESTIMATE_CLASS.equals(bp.getName()))
                                bp.nextText();
                            else if (GETITEMESTIMATE_COLLECTIONID.equals(bp.getName()))
                                bp.nextText();
                            else if (GETITEMESTIMATE_ESTIMATE.equals(bp.getName()))
                                bp.nextIntegerContent();
                            else 
                                bp.skipUnknownElement();
                        }
                        // end of collection
                        bp.require(END_TAG, NAMESPACE_GETITEMESTIMATE, GETITEMESTIMATE_COLLECTION);
                    } else 
                        bp.skipUnknownElement();
                }
                // end of response
                bp.require(END_TAG, NAMESPACE_GETITEMESTIMATE, GETITEMESTIMATE_RESPONSE);
            } else 
                bp.skipUnknownElement();
        }
        // end of GetItemEstimate
        bp.require(END_TAG, NAMESPACE_GETITEMESTIMATE, GETITEMESTIMATE_GETITEMESTIMATE);
    }

}
