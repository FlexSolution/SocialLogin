package com.flexsolution.authentication.oauth2.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * comparator for images. Pixes by pixel
 */
public class ImageComparator {

    /**
     * @param is1 image1
     * @param is2 image2
     * @return true if images are the same
     * @throws IOException if an error occurs during reading
     */
    public static boolean compare(InputStream is1, InputStream is2) throws IOException {
        try {
            BufferedImage image1 = ImageIO.read(is1);
            BufferedImage image2 = ImageIO.read(is2);
            PixelGrabber grab1 = new PixelGrabber(image1, 0, 0, -1, -1, false);
            PixelGrabber grab2 = new PixelGrabber(image2, 0, 0, -1, -1, false);
            int[] data1 = grabPixels(grab1);
            int[] data2 = grabPixels(grab2);

            return Arrays.equals(data1, data2);

        } catch (InterruptedException e) {
            /* continue here */
            e.printStackTrace();
        }
        return false;
    }

    private static int[] grabPixels(PixelGrabber grab) throws InterruptedException {
        int[] data = null;

        if (grab.grabPixels()) {
            int width = grab.getWidth();
            int height = grab.getHeight();
            data = new int[width * height]; // define an array size
            data = (int[]) grab.getPixels();
        }
        return data;
    }
}
