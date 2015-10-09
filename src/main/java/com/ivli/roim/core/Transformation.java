
package com.ivli.roim.core;

import java.awt.image.BufferedImage;
/**
 *
 * @author likhachev
 */
public interface Transformation {
    BufferedImage transform (BufferedImage aSourceImage, BufferedImage aDestinationImageThatCanBeNull);
}
