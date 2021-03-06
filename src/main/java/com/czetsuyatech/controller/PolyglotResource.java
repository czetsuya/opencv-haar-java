package com.czetsuyatech.controller;

 import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.czetsuyatech.business.domain.MultipartBody;
import com.czetsuyatech.business.service.HaarService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;

/**
 * Endpoint for processing an image demonstrating haar classifier application.
 *
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @version 1.0.0
 * @since 1.0.0
 */
@Path("/poly")
public class PolyglotResource {

	public static Context context = Context.newBuilder().allowIO(true).allowAllAccess(true).build();

	private Logger log = org.slf4j.LoggerFactory.getLogger(PolyglotResource.class);

	@Inject
	HaarService haarService;

	private Map<String, byte[]> mapOfImages = new ConcurrentHashMap<>();

	private String basePath;

	@PostConstruct
	public void init() {

		try {
			basePath = new File(".").getCanonicalPath();

		} catch (IOException e) {
			// ignore
		}
		log.debug("basePath={}", basePath);
	}

	/**
	 * Accepts a multipart post that contains an image. This image is process using haar classifier algorithm to detect an object given a stop data.
	 *
	 * @param data posted data that contains an image file and filename
	 * @return Response object that contains an image
	 * @throws IOException when base path and stop data does not exists and when image conversion fails as well
	 */
	@Operation(summary = "Accepts a multipart post that contains an image. This image is process using haar classifier algorithm to detect an object given a stop data.")
	@Path("/objects/detect")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("image/png")
	public Response detectObject(
		@RequestBody(name = "data", description = "Multipart form that contains an image to be process", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MultipartBody.class))) @Valid @MultipartForm MultipartBody data)
		throws IOException {

		byte[] image;

		// check if image has already been processed
		if (!mapOfImages.containsKey(data.getFileName())) {
			mapOfImages.put(data.getFileName(), haarService.processImage(basePath, data));
		}

		image = mapOfImages.get(data.getFileName());

		// uncomment line below to send non-streamed
		// return Response.ok(imageData).build();

		// uncomment line below to send streamed
		return Response.ok(new ByteArrayInputStream(image)).build();
	}

	/**
	 * This endpoint applies the haar classifier algorithm to an image using a stop data that both exist in the project.
	 *
	 * @return returns information on how many objects are detected. By default it returns 1.
	 * @throws IOException when base path and stop data does not exists and when image conversion fails as well
	 */
	@Operation(summary = "This endpoint applies the haar classifier algorithm to an image using a stop data that both exist in the project.")
	@Path("/objects/detect/test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String detectObjectTest() throws IOException {

		String base = new File(".").getCanonicalPath();
		log.debug("basePath={}", base);

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		CascadeClassifier faceDetector = new CascadeClassifier(base + "/src/main/resources/stop_data.xml");

		Mat image = Imgcodecs.imread(base + "/src/main/resources/image.jpg");

		MatOfRect faceVectors = new MatOfRect();
		faceDetector.detectMultiScale(image, faceVectors);

		for (Rect rect : faceVectors.toArray()) {
			Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
				new Scalar(0, 255, 0));
		}

		Imgcodecs.imwrite("detectedObject.png", image);

		int nObjectDetected = faceVectors.toArray().length;
		log.debug("{} objects detected", nObjectDetected);

		return nObjectDetected + " object/s detected";
	}

	/**
	 * Test endpoint that runs js code inside GraalVM.
	 *
	 * @return string confirmation
	 */
	@Path("/js")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloJs() {

		context.eval(Source.create("js", "console.log('hello js')"));
		return "Running JS script console.log('hello js'). Check the logs.";
	}

	/**
	 * Test endpoint that runs py code inside GraalVM.
	 *
	 * @return a value that is return from an executed python script
	 */
	@Path("/py")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloPy() throws IOException {

		final URL pyTestPy = getClass().getClassLoader().getResource("pytest.py");
		System.out.println("" + pyTestPy);
		Value effectValue = context.eval(Source.newBuilder("python", pyTestPy).build());
		return "Result read from pytest.py=" + effectValue.toString();
	}
}
