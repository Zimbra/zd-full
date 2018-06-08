/*
 * 
 */


package com.zimbra.kabuki.tools.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;

public abstract class DecodedImage {

    //
    // Data
    //

	protected String mFilename;
    
    //
    // Constructors
    //

    public DecodedImage(String filename) {
        mFilename = filename;
    }

    //
    // Public methods
    //

    public abstract BufferedImage getBufferedImage();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void load() throws java.io.IOException;

	public String getName() {
		String fileName = mFilename;
		String fileNameBase = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

		// Strip the extension.
	    fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));

		// Strip any "repeat*" tiling derectives.  (Static layout has no directive.)
        for (ImageLayout layout : ImageLayout.values()) {
            if (layout.equals(ImageLayout.NONE)) continue;
            if (fileNameBase.endsWith(layout.toExtension())) {
                fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));
                break;
            }
        }

		return fileNameBase;
	}

    public String getFilename() {
        return mFilename;
    }

} // class DecodedImage