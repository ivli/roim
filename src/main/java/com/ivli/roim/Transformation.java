
package com.ivli.roim;

import java.awt.image.BufferedImage;
/**
 *
 * @author likhachev
 */
public interface Transformation {
    BufferedImage transform (BufferedImage aSrc, BufferedImage aDst);
}
