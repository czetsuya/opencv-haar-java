package com.czetsuyatech.business.domain;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Post parameter to for submitting an image.
 *
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class MultipartBody {

    /**
     * The file parameter.
     */
    @Parameter(description = "Image file where object detection will be applied")
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream file;

    /**
     * Filename use in backend.
     */
    @Parameter(description = "File name of the image")
    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }
}
