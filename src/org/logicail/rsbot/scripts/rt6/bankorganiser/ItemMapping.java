package org.logicail.rsbot.scripts.rt6.bankorganiser;

import org.logicail.cache.loader.rt6.wrapper.ItemDefinition;
import com.sk.cache.wrappers.loaders.WrapperLoader;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 15:44
 */
public class ItemMapping extends IClientAccessor {
	private Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();

	public int getId(int id) {
		return map.containsKey(id) ? map.get(id) : id;
	}

	/// Map Noted => Real id
	public ItemMapping(IClientContext ctx) {
		super(ctx);
		final WrapperLoader<ItemDefinition> loader = ctx.definitions.system().itemLoader;
		// map of items ids to real id
		//try (PrintWriter items = new PrintWriter(new File("items.txt"))) {
		int id = 0;
		while (loader.canLoad(id)) {
			final ItemDefinition definition = loader.load(id);
			//if (definition.clientScriptData != null) {
			//items.println(definition);
			//}
			if (definition.lent) {
				map.put(id, definition.lentId);
			} else if (definition.noted) {
				map.put(id, definition.noteId);
			} else if (definition.cosmetic) {
				map.put(id, definition.cosmeticId);
			}
			// Exclude when id == id
			//map.put(definition.noteId, id);
			
			id++;
		}
		//} catch (FileNotFoundException e) {
		//	e.printStackTrace();
		//}
	}
}
