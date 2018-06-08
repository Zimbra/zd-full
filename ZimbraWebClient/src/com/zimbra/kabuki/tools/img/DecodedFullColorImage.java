/*
 * 
 */


package com.zimbra.kabuki.tools.img;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/*
 * DecodedFullColorImage represents a single PNG/JPG image that will be combined 
 * later.  It knows the original image's height, width, source filename, and 
 * target coordinates in the combined image.
 */
public class DecodedFullColorImage extends DecodedImage {

    //
    // Data
    //

    private BufferedImage mBufImg;

    //
    // Constructors
    //

    public DecodedFullColorImage(String filename) {
        super(filename);
    }

    //
    // DecodedImage methods
    //

    public BufferedImage getBufferedImage() { return mBufImg; }

    public int getWidth() { return mBufImg.getWidth(); }
    public int getHeight() { return mBufImg.getHeight(); }

    /*
     * Load the contents of this image
     */
    public void load() throws IOException {
        String name = getFilename();
        int index = name.lastIndexOf('.');
        String suffix = index != -1 ? name.substring(index + 1) : "";
        Iterator iter = ImageIO.getImageReadersBySuffix(suffix);
        ImageReader reader = (ImageReader) iter.next();
        // make the input file be the input source for the ImageReader (decoder)
        reader.setInput(new FileImageInputStream(new File(mFilename)));
        mBufImg = reader.read(0);
    }

} // class DecodedFullColorImage