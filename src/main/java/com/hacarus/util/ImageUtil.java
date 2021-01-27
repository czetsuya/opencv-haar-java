package com.hacarus.util;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods for image manipulation and conversion.
 *
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageUtil {

    /**
     * Converts a @{link BufferedImage} to @{@link Mat}.
     *
     * @param image the buffered image
     * @return converted image to mat format
     * @throws IOException when conversion fails
     */
    public static Mat bufferedImageToMat(BufferedImage image) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_COLOR);
    }

    /**
     * Converts
     *
     * @param mat @{link Mat} to @{link BufferedImage}.
     * @return converted mat to image
     * @throws IOException when conversion fails
     */
    public static BufferedImage matToBufferedImage(Mat mat) throws IOException {

        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    /**
     * Converts an input stream to buffered image.
     *
     * @param is input stream
     * @return converted is to buffered image
     * @throws IOException when conversation fails
     */
    public static BufferedImage inputStreamToBufferedImage(InputStream is) throws IOException {
        return ImageIO.read(is);
    }

    /**
     * Converts buffered image to byte[].
     *
     * @param bi buffered image
     * @return array of bytes
     * @throws IOException when conversion fails
     */
    public static byte[] bufferedImageToByteArray(BufferedImage bi) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos.toByteArray();
    }
}
