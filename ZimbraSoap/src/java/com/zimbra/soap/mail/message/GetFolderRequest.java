/*
 * 
 */

package com.zimbra.soap.mail.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.mail.type.Folder;

/*
<GetFolderRequest [visible="0|1"] [needGranteeName="0|1"]>
  [<folder [l="{base-folder-id}"] [path="{fully-qualified-path}"]/>]
</GetFolderRequest>

 */
@XmlRootElement(name="GetFolderRequest")
@XmlType(propOrder = {})
public class GetFolderRequest {

    @XmlAttribute(name="visible") private Boolean isVisible;
    @XmlAttribute private boolean needGranteeName;
    @XmlElement private Folder folder;
    
    public GetFolderRequest() {
    }
    
    public GetFolderRequest(Folder folder) {
        this(folder, null);
    }
    
    public GetFolderRequest(Folder folder, Boolean isVisible) {
        setFolder(folder);
        setVisible(isVisible);
    }
    
    public Boolean isVisible() {
        return isVisible;
    }
    
    public boolean isNeedGranteeName() {
        return needGranteeName;
    }
    
    public Folder getFolder() {
        return folder;
    }
    
    public void setVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    public void setNeedGranteeName(boolean needGranteeName) {
        this.needGranteeName = needGranteeName;
    }
    
    public void setFolder(Folder folder) {
        this.folder = folder;
    }
}
