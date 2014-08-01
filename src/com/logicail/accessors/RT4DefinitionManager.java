package com.logicail.accessors;

import com.logicail.DefinitionCache;
import com.logicail.loader.rt4.RT4CacheSystem;
import com.logicail.loader.rt4.wrappers.ItemDefinition;
import com.logicail.loader.rt4.wrappers.NpcDefinition;
import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import com.logicail.loader.rt4.wrappers.Script;
import org.powerbot.script.rt4.*;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 15:29
 */
public class RT4DefinitionManager extends ClientAccessor {
	private final DefinitionCache<ClientContext, NpcDefinition> npc;
	private final DefinitionCache<ClientContext, Script> varp;
	private final DefinitionCache<ClientContext, ObjectDefinition> object;
	private final DefinitionCache<ClientContext, ItemDefinition> item;
	private final RT4CacheSystem system;

	public RT4CacheSystem system() {
		return system;
	}

	public RT4DefinitionManager(ClientContext ctx) throws FileNotFoundException {
		super(ctx);

		String directory = System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator;
		system = new RT4CacheSystem(new File(directory));

		npc = new DefinitionCache<ClientContext, NpcDefinition>(this.ctx, system.npcLoader);
		varp = new DefinitionCache<ClientContext, Script>(this.ctx, system.varpLoader);
		object = new DefinitionCache<ClientContext, ObjectDefinition>(this.ctx, system.objectLoader);
		item = new DefinitionCache<ClientContext, ItemDefinition>(this.ctx, system.itemLoader);
	}

	public NpcDefinition get(Npc npc) {
		return this.npc.get(npc.id());
	}

	public ObjectDefinition get(GameObject npc) {
		return this.object.get(npc.id());
	}

	public ItemDefinition get(Item npc) {
		return this.item.get(npc.id());
	}

	public NpcDefinition npc(int id) {
		return npc.get(id);
	}

	public ObjectDefinition object(int id) {
		return object.get(id);
	}

	public Script varp(int scriptId) {
		return this.varp.get(scriptId);
	}
}
