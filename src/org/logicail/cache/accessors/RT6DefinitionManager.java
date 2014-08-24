package org.logicail.cache.accessors;

import org.logicail.cache.DefinitionCache;
import org.logicail.cache.loader.rt6.RT6CacheSystem;
import org.logicail.cache.loader.rt6.wrapper.ItemDefinition;
import com.sk.cache.DataSource;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GroundItem;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 15:28
 */
public class RT6DefinitionManager extends ClientAccessor {
	private final DefinitionCache<ClientContext, ItemDefinition> item;
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

	}

	public ItemDefinition item(int id) {
		return item.get(id);
	}
}
