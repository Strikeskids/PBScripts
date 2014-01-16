package org.logicail.rsbot.scripts.loggildedaltar;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 16/01/14
 * Time: 16:24
 */
public class FileLogger extends PrintStream {
	public FileLogger(OutputStream out) {
		super(out);
	}
}
