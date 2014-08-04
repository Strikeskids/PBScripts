package org.logicail.rsbot.scripts.rt6.bankorganiser;

import com.logicail.loader.rt6.wrapper.Category;
import com.logicail.loader.rt6.wrapper.ItemDefinition;
import com.logicail.loader.rt6.wrapper.Parameter;
import com.logicail.loader.rt6.wrapper.loaders.ItemDefinitionLoader;
import com.logicail.loader.rt6.wrapper.requirements.CombatSkillRequirement;
import com.logicail.loader.rt6.wrapper.requirements.ItemRequirement;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.rt6.bankorganiser.tasks.ItemSorter;
import org.powerbot.script.rt6.Item;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 04/03/14
 * Time: 10:16
 */
public class ItemCategoriser extends IClientAccessor {
	private final Map<String, LinkedHashSet<Integer>> categoryToItems = new LinkedHashMap<String, LinkedHashSet<Integer>>();
	private final Map<Integer, String> itemToCategory = new LinkedHashMap<Integer, String>();

	//private TreeMap<String, List<ItemDefinition>> categories = new TreeMap<String, List<ItemDefinition>>(); // Remove storing item defs for script
	//private Map<Integer, Category> resources = new HashMap<>();
	private Set<String> potions = new HashSet<String>();
	final Pattern potionPattern = Pattern.compile("(^[a-zA-Z -']+)");
	final Pattern keyPattern = Pattern.compile("(^Key$| key$| key |^Keys$|^Key )");
	private Set<Integer> skipResources = new HashSet<Integer>(); // Dungeoneering etc
	private long version = System.currentTimeMillis();
	private final ItemMapping mapping;
	private Comparator<? super Item> sorter = null;


	public Comparator<? super Item> getSorter() {
		return sorter;
	}

	/**
	 * Let/noted etc to id
	 *
	 * @param id
	 * @return
	 */
	public int id(int id) {
		return mapping.getId(id);
	}

	public ItemCategoriser(IClientContext ctx) {
		super(ctx);
		mapping = new ItemMapping(ctx);
		ItemDefinitionLoader loader = ctx.definitions.system().itemLoader;

		// Load resources first
		for (int id = 0; id < 0xfffff; id++) {
			if (loader.canLoad(id)) {
				final ItemDefinition definition = loader.load(id);
				if (definition.hasParameter(Parameter.DUNGEONEERING)) {
					skipResources.add(id);
					continue;
				}
				if (definition.category() == Category.POTIONS) {
					final Matcher matcher = potionPattern.matcher(definition.name);
					if (matcher.find()) {
						potions.add(matcher.group(1).trim());
					}
				}
			}
		}

		LinkedHashSet<Integer> all = new LinkedHashSet<Integer>();

		for (int id = 0; id < 0xfffff; id++) {
			if (loader.canLoad(id)) {
				final ItemDefinition definition = loader.load(id);

				if (definition.noted || definition.lent || definition.name == null || definition.name.equals("null") || definition.name.length() == 0 || definition.cosmetic) {
					continue;
				}

				if (definition.hasAnyParameter(Parameter.DUNGEONEERING, Parameter.DEFEATED_MOBILISING_ARMIES, Parameter.MOBILISING_ARMIES)) {
					continue;
				}

				all.add(id);

				categorise(definition);
			}
		}

		sorter = new ItemSorter(this, new ArrayList<Integer>(all));
	}

	private void categorise(ItemDefinition definition) {
		if (definition.hasParameter(Parameter.RARE)) {
			add("RARE", definition);
			return;
		}

		for (ItemRequirement requirement : definition.resourceRequirement()) {
			if (skipResources.contains(requirement.getId())) {
				return;
			}
		}

		switch (definition.category()) {
			case MINING_AND_SMITHING:
				if (definition.name.endsWith(" bar")) {
					add("BARS", definition);
				} else {
					add("ORES", definition);
				}
				return;
			case RUNES_SPELLS_TELEPORTS:
				if (definition.name.endsWith(" rune")) {
					add("RUNES", definition);
				} else if (definition.name.contains(" sceptre ")) {
					add(Category.MAGE_WEAPONS, definition);
				} else {
					add("MAGIC_TABLETS_AND_SCROLLS", definition);
				}
				return;
			case POCKET_ITEMS:
				add("POCKET", definition);
				return;
			case MISCELLANEOUS:
				if (definition.hasParameter(Parameter.SEAFARING_STAT)) {
					return;
				}
				if (definition.hasAnyParameter(Parameter.EMOTE_CAPE, Parameter.SKILLCAPE_SKILL)) {
					add("SKILLCAPE", definition); // inc veteran etc.
					return;
				}
				if (definition.hasAnyParameter(Parameter.MELEE_ARMOUR)) {
					add("MELEE_ARMOUR_" + getMeleeTier(definition), definition);
					return;
				}
				if (definition.hasParameter(Parameter.MELEE_WEAPON)) {
					add("MELEE_WEAPONS_" + getMeleeTier(definition), definition);
					return;
				}
				if (definition.hasParameter(Parameter.MAGE_ARMOUR)) {
					add("MAGE_ARMOUR", definition);
					return;
				}
				if (definition.hasParameter(Parameter.RANGE_ARMOUR)) {
					add("RANGE_ARMOUR", definition);
					return;
				}
				//if (definition.hasParameter(Parameter.NEUTRAL_GEAR)) {
				//	add("NEUTRAL_GEAR", definition);
				//	return;
				//}
				if (definition.hasParameter(Parameter.STRANGE_ROCK_SKILL)) {
					add("STRANGE_ROCK", definition);
					return;
				}
				if (definition.hasParameter(Parameter.PET_TYPE)) {
					add("PET", definition);
					return;
				}
				if (definition.hasAnyParameter(Parameter.AURA, Parameter.AURA_COOLDOWN_TIME, Parameter.AURA_LASTING_TIME)) {
					add("AURA", definition);
					return;
				}
				if (definition.slotEnum != null) {
					switch (definition.slotEnum) {
						case NECK:
						case RING:
							add(Category.JEWELLERY, definition);
							return;
						case QUIVER:
							if (definition.name.contains(" arrow") || definition.name.startsWith("Arrows")) {
								add(Category.ARROWS, definition);
								return;
							}
							if (definition.name.contains(" bolt")) {
								add(Category.BOLTS, definition);
								return;
							}
							return;
						default:
							add(String.valueOf(definition.slotEnum), definition);
							return;
					}

				}
				if (urn(definition)) return;
				if (definition.name.startsWith("Casket ") || definition.name.startsWith("Clue scroll ") || definition.name.equals("Scroll") || definition.name.startsWith("Scroll box (")) {
					add("CLUE_SCROLL", definition);
					return;
				}
				if (definition.slot == -1 && definition.name.endsWith(" charm")) {
					add("CHARMS", definition);
					return;
				}
				final Matcher matcher = potionPattern.matcher(definition.name);
				if (matcher.find()) {
					if (potions.contains(matcher.group(1).trim())) {
						add(Category.POTIONS, definition);
						return;
					}
				}
				if (definition.unknowns().containsKey(3744) && definition.unknowns().containsKey(2281)) {
					add(Category.POTIONS, definition);
					return;
				}
				if (contains(definition.actions, "Add-to-toolbelt")) {
					add(Category.TOOLS_AND_CONTAINERS, definition);
					return;
				}
				if (keyPattern.matcher(definition.name).find()) {
					add("KEYS", definition);
					return;
				}
				if (definition.hasParameter(Parameter.QUEST_ITEM) && definition.clientScriptData.size() == 1) {
					if (definition.name.equals("Coins")) {
						add("CURRENCY", definition);
					} else {
						add(Parameter.QUEST_ITEM.name(), definition);
					}
					return;
				}
				if (definition.creationSkill() != null) {
					switch (definition.creationSkill().getSkillEnum()) {
						case COOKING:
							if (definition.name.startsWith("Burnt ")) {
								return;
							}
							if (contains(definition.actions, "Eat") || contains(definition.actions, "Drink")) {
								add(Category.FOOD_AND_DRINK, definition);
								return;
							}
							add(Category.COOKING_INGREDIENTS, definition);
							return;
						case CRAFTING:
							add(Category.CRAFTING_MATERIALS, definition);
							return;
						case DIVINATION:
							add("DIVINATION_PRODUCE", definition);
							return;
						case FARMING:
							add("FARMING_SEEDLING", definition);
							return;
						case FIREMAKING:
							if (definition.name.equals("Hardened straight root")) {
								add(Category.WOODCUTTING_PRODUCE, definition);
								return;
							}
						case FLETCHING:
							add(Category.FLETCHING_MATERIALS, definition);
							return;
						case HERBLORE:
							add(Category.HERBLORE_MATERIALS, definition);
							return;
						case MAGIC:
							if (definition.name.endsWith(" ore")) {
								add("ORES", definition);
								return;
							} else if (definition.name.endsWith(" x5")) {
								return;
							}
						case SMITHING:
							if (definition.name.endsWith(" bar")) {
								add("BARS", definition);
								return;
							} else if (definition.name.endsWith(" arrowheads")) {
								add(Category.ARROWS, definition);
								return;
							} else if (definition.name.endsWith(" bolts (unf)")) {
								add(Category.BOLTS, definition);
								return;
							}
							return;
						case SUMMONING:
							// pouches ending (u) - uncharged, content removed from game
							return;
						default:
							if (!definition.hasParameter(Parameter.QUEST_ITEM)) {
								add(definition.category() + "_" + definition.creationSkill().getSkillEnum(), definition);
							}
							return;
					}
				}
				if (contains(definition.actions, "Read") || contains(definition.actions, "Copy to log")) {
					add("READABLE", definition);
					return;
				}
				if (definition.name.startsWith("Burnt ")) {
					return;
				}
				if (definition.name.endsWith(" ore")) {
					add("ORES", definition);
					return;
				}
				if (definition.name.startsWith("Ascension ")) {
					add("ASCENSION", definition);
					return;
				}
				if (definition.name.endsWith(" seed")) {
					add(Category.SEEDS, definition);
					return;
				}
				if (definition.name.endsWith(" seedling")) {
					add("FARMING_SEEDLING", definition);
					return;
				}
				if (definition.name.endsWith(" sapling")) {
					add("FARMING_SAPLING", definition);
					return;
				}
				if (definition.stackOffset == 1) {
					if (definition.name.endsWith("oins") || definition.name.equals("Tokkul") || definition.name.endsWith("ticket") || definition.name.endsWith("token")) {
						add("CURRENCY", definition);
						return;
					}
				}
				if (definition.name.equals("Bonemeal")) {
					add(Category.PRAYER_MATERIALS, definition);
					return;
				}
				if (definition.name.endsWith(" pouch")) {
					if (contains(definition.actions, "Fill") && contains(definition.actions, "Empty")) {
						add("RUNECRAFTING_POUCHES", definition);
						return;
					}
				}
				if (definition.name.endsWith(" effigy")) {
					add("EFFIGY", definition);
					return;
				}
				add("UNKNOWN", definition);
				//add("UNKNOWN_" + definition.categoryId, definition);
				return;
			//break;
			case TOOLS_AND_CONTAINERS:
				if (urn(definition)) return;
		}

		add(String.valueOf(definition.category()), definition);
	}

	private void add(Category category, ItemDefinition definition) {
		add(String.valueOf(category), definition);
	}

	private boolean urn(ItemDefinition definition) {
		if (definition.name.contains(" urn (")) {
			add("URN", definition);
			return true;
		}
		return false;
	}

	private String getMeleeTier(ItemDefinition definition) {
		if (definition.wieldRequirements().size() >= 3) {
			return "HIGH_LEVEL";
		}

		for (CombatSkillRequirement requirement : definition.wieldRequirements()) {
			if (requirement.getLevel() <= 45) {
				return "LOW_LEVEL";
			} else if (requirement.getLevel() < 70) {
				return "MID_LEVEL";
			} else {
				return "HIGH_LEVEL";
			}
		}


		return "LOW_LEVEL";
	}

	public static String prettyName(String value) {
		if (value == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder(value.length());
		if (value.length() > 0) {
			sb.append(Character.toUpperCase(value.charAt(0)));
		}
		if (value.length() > 1) {
			sb.append(value.substring(1).toLowerCase());
		}

		return sb.toString().replace('_', ' ');
	}

//	public void write(PrintWriter out) throws IOException {
//		JsonObject json = new JsonObject();
//		json.add("version", version);
//
//		// ItemMapping
//		mapping.write(json);
//
//		JsonObject categories = new JsonObject();
//
//		for (Map.Entry<String, List<ItemDefinition>> entry : this.categories.entrySet()) {
//			final List<ItemDefinition> definitions = entry.getValue();
//			Collections.sort(definitions, new ItemNameSorter());
//
//			if (!entry.getKey().equals("UNKNOWN")) {
//				JsonArray ids = new JsonArray();
//				for (ItemDefinition definition : definitions) {
//					ids.add(definition.getId());
//				}
//
//				categories.add(prettyName(entry.getKey()), ids);
//			}
//		}
//
//		json.add("categories", categories);
//
//		out.write(json.toString());
//	}

	private void add(final String category, final ItemDefinition value) {
		String pretty = prettyName(category);

		itemToCategory.put(value.id, pretty);
		if (!categoryToItems.containsKey(pretty)) {
			categoryToItems.put(pretty, new LinkedHashSet<Integer>() {{
				add(value.id);
			}});
		} else {
			categoryToItems.get(pretty).add(value.id);
		}
	}

	public ArrayList<String> getCategorys() {
		ArrayList<String> categories = new ArrayList<String>(categoryToItems.keySet());
		Collections.sort(categories);
		return categories;
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

	class ItemNameSorter implements Comparator<ItemDefinition> {
		@Override
		public int compare(ItemDefinition lhs, ItemDefinition rhs) {
			if (lhs.name == null || rhs.name == null) {
				return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());
			}

			return lhs.name.compareTo(rhs.name);
		}
	}

	public String category(int id) {
		return itemToCategory.get(id(id));
	}

	public static boolean contains(String[] values, String needle) {
		for (String value : values) {
			if (value == null) {
				continue;
			}
			if (value.equals(needle)) {
				return true;
			}
		}
		return false;
	}
}
