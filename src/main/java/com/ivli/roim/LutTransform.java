
package com.ivli.roim;

import java.awt.image.BufferedImage;
/**
 *
 * @author likhachev
 */
public interface LutTransform {
    public BufferedImage transform (BufferedImage aSrc, BufferedImage aDst);
}
