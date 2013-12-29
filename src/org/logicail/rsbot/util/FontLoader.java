package org.logicail.rsbot.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/12/13
 * Time: 21:22
 */
public class FontLoader {
	public static Font load(File file) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(Font.PLAIN, 14);
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			graphicsEnvironment.registerFont(font);
			return font;
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Font("Arial", Font.BOLD, 14);
	}
}
