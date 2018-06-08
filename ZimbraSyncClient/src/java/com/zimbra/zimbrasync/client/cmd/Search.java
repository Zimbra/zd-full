/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class Search extends Command {

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }

    //  <Search>
    //      <Status>1</Status>
    //      <Response>
    //          <Store>
    //              <Status>1</Status>
    //              <Result/>
    //          </Store>
    //      </Response>
    //  </Search>
    
    //  <Search>
    //      <Status>1</Status>
    //      <Response>
    //          <Store>
    //              <Status>1</Status>
    //              <Result>
    //                  <Properties>
    //                      <DisplayName>Jimbob Foley</DisplayName>
    //                      <Phone>4085551212</Phone>
    //                      <Office>Engineering</Office>
    //                      <Title>Engineer</Title>
    //                      <Company>Zimbra</Company>
    //                      <Alias>jimbob</Alias>
    //                      <FirstName>Jimbob</FirstName>
    //                      <LastName>Foley</LastName>
    //                      <HomePhone>6503213456</HomePhone>
    //                      <MobilePhone>6507771234</MobilePhone>
    //                      <EmailAddress>jimbob@jjhome.local</EmailAddress>
    //                  </Properties>
    //              </Result>
    //              <Result>
    //                  <Properties>
    //                      <DisplayName>Sue Smith</DisplayName>
    //                      <Alias>sue</Alias>
    //                      <FirstName>Sue</FirstName>
    //                      <LastName>Smith</LastName>
    //                      <EmailAddress>sue@jjhome.local</EmailAddress>
    //                  </Properties>
    //              </Result>
    //              <Range>0-1</Range>
    //              <Total>2</Total>
    //          </Store>
    //      </Response>
    //  </Search>
    
    // <Search xmlns="Search:">
    // <Status>1</Status>
    // <Response>
    //     <Store>
    //         <Status>1</Status>
    //         <Result>
    //             <Class xmlns="AirSync:">Email</Class>
    //             <LongId>RgAAAACLRgqWNALmQL9b58vi%2bDAGBwDPbWk31iWOTa1BB6%2bDpjkOAAAAAAAWAADPbWk31iWOTa1BB6%2bDpjkOAAAAADLmAAAJ</LongId>
    //             <CollectionId xmlns="AirSync:">5</CollectionId>
    //             <Properties>
    //                 <To xmlns="Email:" bytes="30"/>
    //                 <From xmlns="Email:" bytes="30"/>
    //                 <Subject xmlns="Email:" bytes="9"/>
    //                 <DateReceived xmlns="Email:">2010-08-07T00:05:52.667Z</DateReceived>
    //                 <DisplayTo xmlns="Email:" bytes="5"/>
    //                 <ThreadTopic xmlns="Email:" bytes="9"/>
    //                 <Importance xmlns="Email:">1</Importance>
    //                 <Read xmlns="Email:">1</Read>
    //                 <Body=33 bytes/>
    //                 <MessageClass xmlns="Email:">IPM.Note</MessageClass>
    //                 <InternetCPID xmlns="Email:">28591</InternetCPID>
    //                 <Flag xmlns="Email:"/>
    //                 <ContentClass xmlns="Email:">urn:content-classes:message</ContentClass>
    //                 <NativeBodyType xmlns="AirSyncBase:">2</NativeBodyType>
    //             </Properties>
    //         </Result>
    //         <Range>0-0</Range>
    //         <Total>1</Total>
    //     </Store>
    // </Response>
    // </Search>
    
    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_SEARCH, SEARCH_SEARCH);
        status = bp.nextIntegerElement(NAMESPACE_SEARCH, SEARCH_STATUS);
        
        if (status == 1) {
            bp.openTag(NAMESPACE_SEARCH, SEARCH_RESPONSE);
            bp.openTag(NAMESPACE_SEARCH, SEARCH_STORE);
            bp.nextIntegerElement(NAMESPACE_SEARCH, SEARCH_STATUS);
            while (bp.next() == START_TAG && NAMESPACE_SEARCH.equals(bp.getNamespace())) {
                if (SEARCH_RESULT.equals(bp.getName())) {
                    while (bp.next() == START_TAG) {
                        if (SEARCH_PROPERTIES.equals(bp.getName()) && NAMESPACE_SEARCH.equals(bp.getNamespace()))
                            bp.skipElement();
                        else if (AIRSYNC_CLASS.equals(bp.getName()) && NAMESPACE_AIRSYNC.equals(bp.getNamespace()))
                            bp.nextText();
                        else if (SEARCH_LONGID.equals(bp.getName()) && NAMESPACE_SEARCH.equals(bp.getNamespace()))
                            bp.nextText();
                        else if (AIRSYNC_COLLECTIONID.equals(bp.getName()) && NAMESPACE_AIRSYNC.equals(bp.getNamespace()))
                            bp.nextText();
                    }
                    bp.require(END_TAG, NAMESPACE_SEARCH, SEARCH_RESULT);
                } else if (SEARCH_TOTAL.equals(bp.getName())) {
                    bp.nextText();
                } else if (SEARCH_RANGE.equals(bp.getName())) {
                    bp.nextText();
                }
            }
            bp.require(END_TAG, NAMESPACE_SEARCH, SEARCH_STORE);
            bp.closeTag(); //Response
        }
        
        bp.closeTag(); //Search
    }

}
