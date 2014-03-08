package org.logicail.rsbot.scripts.bankorganiser;

import org.logicail.rsbot.scripts.bankorganiser.gui.BankOrganiserInterface;
import org.logicail.rsbot.scripts.bankorganiser.tasks.ItemSorter;
import org.powerbot.script.wrappers.Item;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 02/03/14
 * Time: 16:05
 */
public class ItemData {
	public static final Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	private static final Map<Integer, String> itemToCategory = new HashMap<Integer, String>();
	private static final Map<String, LinkedHashSet<Integer>> categoryToItems = new LinkedHashMap<String, LinkedHashSet<Integer>>();
	private static String ITEMDATA_ADDRESS = "http://logicail.co.uk/resources/items.dat";
	private static int version = -1;
	private static boolean loaded;
	private static Comparator<? super Item> sorter = null;

	public static boolean loaded() {
		return loaded;
	}

	public static Set<String> getCategorys() {
		return categoryToItems.keySet();
	}

	static {
		load(ITEMDATA_ADDRESS, "LogItemData");
	}

	private static void load(String url, String useragent) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.addRequestProperty("User-Agent", useragent);
			connection.setRequestProperty("Connection", "close");

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setUseCaches(false);

			DataInputStream data = new DataInputStream(connection.getInputStream());
			version = data.readInt();
			int count = data.readInt();
			for (int i = 0; i < count; i++) {
				mapping.put(data.readUnsignedShort(), data.readUnsignedShort());
			}
			int numberCategories = data.readInt();
			for (int i = 0; i < numberCategories; i++) {
				final String category = BankOrganiserInterface.prettyName(data.readUTF());
				final int ids = data.readInt();
				for (int j = 0; j < ids; j++) {
					final int id = data.readUnsignedShort();
					itemToCategory.put(id, category);
					if (!categoryToItems.containsKey(category)) {
						categoryToItems.put(category, new LinkedHashSet<Integer>());
					}
					categoryToItems.get(category).add(id);
				}
			}

			LinkedHashSet<Integer> set = new LinkedHashSet<Integer>();
			for (Map.Entry<String, LinkedHashSet<Integer>> entry : categoryToItems.entrySet()) {
				set.addAll(entry.getValue());
			}

			sorter = new ItemSorter(new ArrayList<Integer>(set));

			loaded = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static int getId(int id) {
		return mapping.containsKey(id) ? mapping.get(id) : id;
	}

	public static LinkedHashSet<Integer> getData(List<String> categories) {
		LinkedHashSet<Integer> result = new LinkedHashSet<Integer>();

		for (String category : categories) {
			if (categoryToItems.containsKey(category)) {
				result.addAll(categoryToItems.get(category));
			}
		}

		return result;
	}

	public static int getVersion() {
		return version;
	}

	public static Comparator<? super Item> getSorter() {
		return sorter;
	}
}
