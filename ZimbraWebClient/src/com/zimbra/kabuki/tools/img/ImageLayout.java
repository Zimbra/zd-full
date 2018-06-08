/*
 * 
 */

package com.zimbra.kabuki.tools.img;

import java.io.File;

public enum ImageLayout {

    //
    // Values
    //
    NONE("no-repeat", ""),
    TILE("repeat", ".repeat"),
    // NOTE: While it may seem counter-intuitive that a HORIZONTAL image layout
    // NOTE: would have a "repeat-y" CSS value, it is in fact correct. The name
    // NOTE: of the ImageLayout enum value corresponds to how the sub-images are
    // NOTE: arranged within the image map. In order for a sub-image to be
    // NOTE: repeated along the x-axis, they need to be laid out vertically 
    // NOTE: within the generated image.
    HORIZONTAL("repeat-y", ".repeaty"),
    VERTICAL("repeat-x", ".repeatx");

    //
    // Data
    //

    private String css;
    private String ext;

    //
    // Constructors
    //

    ImageLayout(String css, String ext) {
        this.css = css;
        this.ext = ext;
    }

    //
    // Public methods
    //

    public String toCss() {
        return css;
    }

    public String toExtension() {
        return ext;
    }

    public static ImageLayout fromFile(File file) {
        String name = file.getName().toLowerCase();
        for (ImageLayout layout : ImageLayout.values()) {
            if (layout.equals(ImageLayout.NONE)) continue;
            if (name.contains(layout.toExtension())) {
                return layout;
            }
        }
        return ImageLayout.NONE;
    }

} // enum ImageLayout

