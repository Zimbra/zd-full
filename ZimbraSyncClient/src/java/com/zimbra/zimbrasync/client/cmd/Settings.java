/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class Settings extends Command {

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }

    //
    //  <Settings xmlns="Settings">
    //    <Status>1</Status>
    //    <Oof>
    //      <Status>1</Status>
    //      <Get>
    //        <OofState>0</OofState>
    //        <OofMessage>
    //          <AppliesToInternal/>
    //          <Enabled>0</Enabled>
    //          <ReplyMessage>Hi</ReplyMessage>
    //          <BodyType>TEXT</BodyType>
    //        </OofMessage>
    //        <OofMessage>
    //          <AppliesToExternalKnown/>
    //          <Enabled>0</Enabled>
    //        </OofMessage>
    //        <OofMessage>
    //          <AppliesToExternalUnknown/>
    //          <Enabled>0</Enabled>
    //        </OofMessage>
    //      </Get>
    //    </Oof>
    //  </Settings>
    
    /* Response to Set out of office status */
    //  <Settings xmlns="Settings:">
    //    <Status>1</Status>
    //    <Oof>
    //      <Status>1</Status>
    //    </Oof>
    //  </Settings>
    
    /* Response to Set device information */
    //  <Settings xmlns="Settings:">
    //    <Status>1</Status>
    //    <DeviceInformation>
    //      <Status>1</Status>
    //    </DeviceInformation>
    //  </Settings>

    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_SETTINGS, SETTINGS_SETTINGS);
        status = bp.nextIntegerElement(NAMESPACE_SETTINGS, SETTINGS_STATUS);
        
        while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
            if (SETTINGS_DEVICEINFORMATION.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                    if (SETTINGS_STATUS.equals(bp.getName()))
                        bp.nextText(); //Status
                    else if (SETTINGS_GET.equals(bp.getName())) {
                        while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                            if (SETTINGS_MODEL.equals(bp.getName()))
                                bp.nextText(); //Model
                            else if (SETTINGS_IMEI.equals(bp.getName()))
                                bp.nextText(); //IMEI
                            else if (SETTINGS_FRIENDLYNAME.equals(bp.getName()))
                                bp.nextText(); //FriendlyName
                            else if (SETTINGS_OS.equals(bp.getName()))
                                bp.nextText(); //OS
                            else if (SETTINGS_OSLANGUAGE.equals(bp.getName()))
                                bp.nextText(); //OSLanguage
                            else if (SETTINGS_PHONENUMBER.equals(bp.getName()))
                                bp.nextText(); //PhoneNumber
                            else
                                bp.skipUnknownElement();
                        }
                        //end of DeviceInformation Get
                        bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_GET);
                    } else
                        bp.skipElement();
                }
                //end of DeviceInformation
                bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_DEVICEINFORMATION);
            } else if (SETTINGS_USERINFORMATION.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                    if (SETTINGS_STATUS.equals(bp.getName()))
                        bp.nextText(); //Status
                    else if (SETTINGS_GET.equals(bp.getName())) {
                        while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                            if (SETTINGS_EMAILADDRESSES.equals(bp.getName())) {
                                while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                                    if (SETTINGS_SMTPADDRESS.equals(bp.getName()))
                                        bp.nextText(); //SmtpAddress
                                    else
                                        bp.skipElement();
                                }
                                //end of EmailAddresses
                                bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_EMAILADDRESSES);
                            } else
                                bp.skipElement();
                        }
                        //end of UserInformation Get
                        bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_GET);
                    } else
                        bp.skipElement();
                }
                //end of UserInformation
                bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_USERINFORMATION);
            } else if (SETTINGS_OOF.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                    if (SETTINGS_STATUS.equals(bp.getName()))
                        bp.nextText(); //Status
                    else if (SETTINGS_GET.equals(bp.getName())) {
                        while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                            if (SETTINGS_OOFSTATE.equals(bp.getName()))
                                bp.nextIntegerContent(); //OofState
                            else if (SETTINGS_STARTTIME.equals(bp.getName()))
                                bp.nextText(); //StartTime
                            else if (SETTINGS_ENDTIME.equals(bp.getName()))
                                bp.nextText(); //EndTime
                            else if (SETTINGS_OOFMESSAGE.equals(bp.getName())) {
                                while (bp.next() == START_TAG && NAMESPACE_SETTINGS.equals(bp.getNamespace())) {
                                    if (SETTINGS_ENABLED.equals(bp.getName()))
                                        bp.nextIntegerContent(); //Enabled
                                    else if (SETTINGS_APPLIESTOINTERNAL.equals(bp.getName())) {
                                        if (bp.isEmptyElementTag())
                                            bp.closeTag();
                                        else
                                            bp.nextIntegerContent();
                                    } else if (SETTINGS_APPLIESTOEXTERNALKNOWN.equals(bp.getName())) {
                                        if (bp.isEmptyElementTag())
                                            bp.closeTag();
                                        else
                                            bp.nextIntegerContent();
                                    } else if (SETTINGS_APPLIESTOEXTERNALUNKNOWN.equals(bp.getName())) {
                                        if (bp.isEmptyElementTag())
                                            bp.closeTag();
                                        else
                                            bp.nextIntegerContent();
                                    } else if (SETTINGS_BODYTYPE.equals(bp.getName()))
                                        bp.nextText(); //BodyType
                                    else if (SETTINGS_REPLYMESSAGE.equals(bp.getName()))
                                        bp.nextText(); //ReplyMessage
                                    else
                                        bp.skipElement();     
                                }
                                //end of OofMessage
                                bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_OOFMESSAGE);
                            } else
                                bp.skipElement();
                        }
                        //end of Oof Get
                        bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_GET);      
                    } else
                        bp.skipElement();
                }
                //end of Oof
                bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_OOF);  
            }
        }
        bp.require(END_TAG, NAMESPACE_SETTINGS, SETTINGS_SETTINGS);
    }
    

}
