package org.logicail.api;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/13
 * Time: 18:33
 */
public class FileUpload {

	public static BufferedImage getScreenShot() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		try {
			return new Robot().createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void upload(URL destination, byte[] data) {

	}

	static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
