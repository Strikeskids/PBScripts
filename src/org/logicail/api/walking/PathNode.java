package org.logicail.api.walking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 01/07/13
 * Time: 17:31
 */
public class PathNode extends WorldPoint {
	private List<PathLink> pathLinks = new ArrayList<>();

	public PathNode() {
		super(0, 0, 0);
	}

	public List<PathLink> getPathLinks() {
		return pathLinks;
	}
}
