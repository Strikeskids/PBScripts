package org.logicail.rsbot.scripts.bankorganiser;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.bankorganiser.tasks.ItemSorter;
import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.util.IOUtil;
import org.powerbot.script.rt6.Item;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 16:05
 */
public class ItemData extends IClientAccessor {
	public final Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	private final Map<String, LinkedHashSet<Integer>> categoryToItems = new LinkedHashMap<String, LinkedHashSet<Integer>>();
	private static String ITEMDATA_ADDRESS = "http://logicail.co.uk/resources/bankorganiser.json";
	private long version = -1;
	private Comparator<? super Item> sorter = null;

	public boolean loaded() {
		return version > -1;
	}

	public Set<String> getCategorys() {
		return categoryToItems.keySet();
	}

	public ItemData(final IClientContext ctx) {
		super(ctx);
		final File file = ctx.controller.script().download(ITEMDATA_ADDRESS, "bankorganiser.json");
		try {
			final DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

			JsonObject map = JsonObject.readFrom(IOUtil.read(stream));

			for (JsonObject.Member member : map.get("mapping").asObject()) {
				mapping.put(Integer.parseInt(member.getName()), member.getValue().asInt());
			}

			LinkedHashSet<Integer> all = new LinkedHashSet<Integer>();

			for (JsonObject.Member member : map.get("categories").asObject()) {
				final LinkedHashSet<Integer> set = new LinkedHashSet<Integer>();

				for (JsonValue value : member.getValue().asArray()) {
					set.add(value.asInt());
				}

				all.addAll(set);

				categoryToItems.put(member.getName(), set);
			}
			sorter = new ItemSorter(this, new ArrayList<Integer>(all));
			version = map.get("version").asLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getId(int id) {
		return mapping.containsKey(id) ? mapping.get(id) : id;
	}

	public LinkedHashSet<Integer> getData(List<String> categories) {
		LinkedHashSet<Integer> result = new LinkedHashSet<Integer>();

		for (String category : categories) {
			if (categoryToItems.containsKey(category)) {
				result.addAll(categoryToItems.get(category));
			}
		}

		return result;
	}

	public long getVersion() {
		return version;
	}

	public Comparator<? super Item> getSorter() {
		return sorter;
	}
}
