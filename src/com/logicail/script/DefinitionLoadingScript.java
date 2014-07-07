package com.logicail.script;

import com.logicail.wrappers.NpcDefinition;
import com.sk.cache.DataSource;
import com.sk.cache.fs.CacheSystem;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 19:25
 */
@Script.Manifest(name = "Cache loading test", description = "loads definitions from cache", properties = "topic=1194037;client=4;")
public class DefinitionLoadingScript extends PollingScript<ClientContext> implements PaintListener {
	private final CacheSystem system;
	private final DefinitionCache<NpcDefinition> npcCache;

	public DefinitionLoadingScript() throws FileNotFoundException {
		system = new CacheSystem(new DataSource(new File(System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator)));
		npcCache = new DefinitionCache<NpcDefinition>(system.npcLoader);
	}

	@Override
	public void poll() {

	}

	@Override
	public void repaint(Graphics graphics) {
		for (Npc npc : ctx.npcs.select().within(10).select(Interactive.areInViewport())) {
			final NpcDefinition definition = npcCache.get(npc.id());
			if (definition != null) {
				final Point p = npc.centerPoint();
				graphics.drawString(definition.name, p.x, p.y);
				graphics.drawString(Arrays.toString(definition.models), p.x, p.y + 10);
			}
		}
	}
}
