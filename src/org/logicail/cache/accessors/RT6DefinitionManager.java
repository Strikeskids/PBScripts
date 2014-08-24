package org.logicail.cache.accessors;

import com.sk.cache.DataSource;
import org.logicail.cache.DefinitionCache;
import org.logicail.cache.loader.rt6.RT6CacheSystem;
import org.logicail.cache.loader.rt6.wrapper.ItemDefinition;
import org.logicail.cache.loader.rt6.wrapper.ObjectDefinition;
import org.logicail.cache.loader.rt6.wrapper.QuestDefinition;
import org.logicail.cache.loader.rt6.wrapper.Script;
import org.logicail.cache.loader.rt6.wrapper.loaders.QuestDefinitionLoader;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GroundItem;
import org.powerbot.script.rt6.Item;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 15:28
 */
public class RT6DefinitionManager extends ClientAccessor {
	private final DefinitionCache<ClientContext, ItemDefinition> item;
	private final DefinitionCache<ClientContext, ObjectDefinition> object;
	private final DefinitionCache<ClientContext, Script> script;
	private final QuestDefinitionLoader questLoader;
	private final RT6CacheSystem system;

	public RT6CacheSystem system() {
		return system;
	}

	public ItemDefinition get(Item item) {
		return this.item.get(item.id());
	}

	public ItemDefinition get(GroundItem item) {
		return this.item.get(item.id());
	}

	public RT6DefinitionManager(ClientContext ctx) throws FileNotFoundException {
		super(ctx);

		system = new RT6CacheSystem(DataSource.getDefaultCacheDirectory("runescape"));

		item = new DefinitionCache<ClientContext, ItemDefinition>(this.ctx, system.itemLoader);
		object = new DefinitionCache<ClientContext, ObjectDefinition>(this.ctx, system.objectLoader);
		script = new DefinitionCache<ClientContext, Script>(this.ctx, system.scriptLoader);
		questLoader = new QuestDefinitionLoader(system);

	}

	public ItemDefinition item(int id) {
		return item.get(id);
	}

	public ObjectDefinition object(int id) {
		return object.get(id);
	}

	public QuestDefinition quest(String name) {
		return questLoader.find(name);
	}

	public Script script(int id) {
		return script.get(id);
	}
}
