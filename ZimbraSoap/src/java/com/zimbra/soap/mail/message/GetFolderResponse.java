/*
 * 
 */

package com.zimbra.soap.mail.message;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.mail.type.Folder;

/*
<GetFolderResponse>
  <folder ...>
    <folder .../>
    <folder ...>
      <folder .../>
    </folder>
    <folder .../>
    [<link .../>]
    [<search .../>]
  </folder>
</GetFolderResponse>
 */
@XmlRootElement(name="GetFolderResponse")
@XmlType(propOrder = {})
public class GetFolderResponse {

    @XmlElementRef private Folder folder;
    
    public GetFolderResponse() {
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }
}
