package org.logicail.api.walking.webbuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 01/07/13
 * Time: 16:26
 */
public class ImageLoader {
	public static BufferedImage loadImage(final String name, final String url) {
		try {
			final BufferedImage cacheImage = loadFromCache(name);

			if (cacheImage != null) {
				return cacheImage;
			} else {
				return loadFromWeb(name, url);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage loadFromCache(final String name) {
		try {
			return ImageIO.read(new File(name));
		} catch (IOException e) {
			return null;
		}
	}

	private static BufferedImage loadFromWeb(final String name, final String urlString) throws IOException {
		final URLConnection uc = (new URL(urlString)).openConnection();
		uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
		final BufferedImage image = ImageIO.read(uc.getInputStream());

		if (image != null) {
			File file = new File(name);
			if (file.exists()) {
				file.delete();
			}
			ImageIO.write(image, "png", file);
		}

		return image;
	}
}