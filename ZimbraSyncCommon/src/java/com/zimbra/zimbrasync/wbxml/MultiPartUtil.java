/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zimbra.common.util.BigByteBuffer;
import com.zimbra.common.util.ByteUtil;

/**
 * Active Sync multipart body encoding and decoding utilities
 * @author smukhopadhyay
 *
 */
public class MultiPartUtil {
    
    // Gezz...this took me hours to figure out...
    // A multipart sync response needs to be encoded in the following manner:
    //
    // 02 00 00 00 14 00 00 00 88 01 00 00 9c 01 00 00                   
    // 76 36 00 00
    // 
    // First 4 bytes - number of files/document (typically first one is always the wbxml doc)
    // 8 bytes per document: 4 bytes for the start offset and 4 bytes for the length
    // (all the integers are encoded in reverse byte order e.g. 02 00 00 00 is equivalent to
    // 00 00 00 02)
    // Hence, in the above example there are 2 docs; first one starts at offset 20 (00 00 00 14) 
    // and length 392 (00 00 01 88). The second doc starts at offset 412 ...

    public static int encodeMultiPartResponse(List<InputStream> data, OutputStream out) throws IOException {
        int ret = 0;
        int startOffset = 0;
        BigByteBuffer bbb = new BigByteBuffer();
        
        try {
            // encode the number of docs
            byte[] numberOfDocs = intToInverseByteArray(data.size());
            bbb.write(numberOfDocs);
            
            // move the start pointer
            startOffset += numberOfDocs.length;
            startOffset += 8 * data.size(); // 8 bytes per doc.
            
            List<byte[]> docs = new ArrayList<byte[]> (data.size());
            for (int i =0; i < data.size(); i++) {
                byte[] bytes = ByteUtil.getContent(data.get(i), -1);
                docs.add(bytes);
                bbb.write(intToInverseByteArray(startOffset)); // start offset of the doc
                bbb.write(intToInverseByteArray(bytes.length)); // length of the doc
                startOffset += bytes.length; // move the start offset for the next doc
            }
            
            // append the docs
            for (int i =0; i < data.size(); i++)
                bbb.write(docs.get(i));
            
            bbb.doneWriting();
            ByteUtil.copy(bbb.getInputStream(), true, out, false);
        } finally {
            ret = bbb.length();
            bbb.destroy(); // don't forget to destroy!
        }
        return ret;
    }
    
    /**
     * Returns docs list in an byte array. 
     * Note: this is not an memory efficient function; ideally, this should return InputStream (To Do..)
     * @param data
     * @return
     */
    public static List<byte[]> decodeBodyParts(byte[] data) throws IOException {
        int offSet = 0;
        int nDocs = inverseByteArrayToInt(Arrays.copyOfRange(data, offSet, offSet+4));
        offSet += 4;
        
        List<byte[]> docs = new ArrayList<byte[]> (nDocs);
        for (int i =0; i < nDocs; i++) {
            int startOffset = inverseByteArrayToInt(Arrays.copyOfRange(data, offSet, offSet+4));
            offSet += 4;
            int length = inverseByteArrayToInt(Arrays.copyOfRange(data, offSet, offSet+4));
            offSet += 4;
            byte[] doc = Arrays.copyOfRange(data, startOffset, startOffset+length);
            docs.add(doc);
        }   
        return docs;  
    }
    
    public static final byte[] intToInverseByteArray(final int i) throws IOException {
        return new byte[] {(byte)(i & 0x000000FF), (byte)((i & 0x0000FF00) >> 8), (byte)((i & 0x00FF0000) >> 16), (byte)((i & 0xFF000000) >> 24)};
    }
    
    public static final int inverseByteArrayToInt(final byte[] bytes) throws IOException {
        return (bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8 |  (bytes[2] & 0xFF) << 16 |   (bytes[3] & 0xFF) << 24;
    }

}
