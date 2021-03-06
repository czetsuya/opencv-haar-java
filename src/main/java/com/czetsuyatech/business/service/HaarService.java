package com.czetsuyatech.business.service;

import com.czetsuyatech.business.domain.MultipartBody;
import com.czetsuyatech.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;

@ApplicationScoped
public class HaarService {

    private Logger log = LoggerFactory.getLogger(HaarService.class);

    public byte[] processImage(String base, @Valid MultipartBody data) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        CascadeClassifier faceDetector = new CascadeClassifier(base + "/src/main/resources/stop_data.xml");

        Mat matImage = ImageUtil.bufferedImageToMat(ImageUtil.inputStreamToBufferedImage(data.getFile()));

        MatOfRect faceVectors = new MatOfRect();
        faceDetector.detectMultiScale(matImage, faceVectors);

        for (Rect rect : faceVectors.toArray()) {
            Imgproc.rectangle(matImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        BufferedImage bi = ImageUtil.matToBufferedImage(matImage);

        int nObjectDetected = faceVectors.toArray().length;
        log.debug("{} objects detected", nObjectDetected);

        return ImageUtil.bufferedImageToByteArray(bi);
    }
}
