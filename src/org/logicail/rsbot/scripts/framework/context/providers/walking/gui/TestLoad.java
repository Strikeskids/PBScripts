package org.logicail.rsbot.scripts.framework.context.providers.walking.gui;

import org.logicail.rsbot.scripts.framework.context.providers.walking.IWeb;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/02/14
 * Time: 15:06
 */
public class TestLoad {
	public static void main(String[] args) {
		IWeb web = new IWeb(null);
		final boolean loaded = web.load("C:\\Users\\Michael\\Desktop\\tomWeb.dat");
		System.out.println(web.toStringExtended());
	}
}
