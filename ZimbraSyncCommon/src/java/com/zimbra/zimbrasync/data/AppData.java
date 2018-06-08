/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.zimbrasync.wbxml.BinaryCodec;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class AppData extends BinaryCodec {
    
    private List<String> categories;
    
    public void addCategory(String category) {
        if (categories == null) {
            categories = new ArrayList<String>();
        }
        categories.add(category);
    }
    
    public List<String> getCategories() {
        return categories;
    }
    
    public void parseCategories(BinaryParser parser, String nameSpace, String categoryName) 
            throws BinaryCodecException, IOException {
        while (parser.next() == START_TAG) {
            if (categoryName.equals(parser.getName())) {
                addCategory(parser.nextText());
            } else {
                parser.skipUnknownElement();
            }
        }
    }

    public void encodeCategories(BinarySerializer serializer, String nameSpace, String categoriesName, String categoryName)
            throws BinaryCodecException, IOException {
        if (categories != null) {
            serializer.openTag(nameSpace, categoriesName);
            for (String category : categories) {
                serializer.textElement(nameSpace, categoryName, category);
            }
            serializer.closeTag(); //Categories
        }
    }
}
