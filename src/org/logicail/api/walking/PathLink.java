package org.logicail.api.walking;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 01/07/13
 * Time: 17:31
 */
public class PathLink extends TilePath {
	private float length;

	public PathLink(MethodContext arg0, Tile[] arg1) {
		super(arg0, arg1);
	}

	public float getLength() {
		return length;
	}
}
