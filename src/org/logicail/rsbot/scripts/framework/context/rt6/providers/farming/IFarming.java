package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import com.sk.cache.wrappers.loaders.LocalObjectLoader;
import com.sk.cache.wrappers.region.LocalObject;
import com.sk.cache.wrappers.region.LocalObjects;
import org.logicail.cache.loader.rt6.wrapper.ObjectDefinition;
import org.logicail.cache.loader.rt6.wrapper.QuestDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.HerbEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.Herb;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.MobileIdNameQuery;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:31
 */
public class IFarming extends IClientAccessor {
	// TODO: Missing some plots
	public static final int[] HERB = {8150, 8151, 8152, 8153, 18816};
	public static final int BELLADONNA = 7572;
	public static final int[] BUSH = {7577, 7578, 7579, 7580};
	public static final int CACTUS = 7771;
	public static final int CALQUAT = 7807;
	public static final int[] COMPOST = {7836, 7837, 7838, 7839, 56684, 66577};
	public static final int[] FLOWER = {7847, 7848, 7849, 7850};
	public static final int[] FRUIT_TREE = {7962, 7963, 7964, 7965, 28919, 56667};
	public static final int[] HOPS = {8173, 8174, 8175, 8176};
	public static final int MUSHROOM = 8337;
	public static final int[] SPIRIT_TREE = {8338, 8382, 8383};
	public static final int[] TREE = {8388, 8389, 8390, 8391};
	public static final int[] ALLOTMENT = {8550, 8551, 8552, 8553, 8554, 8555, 8556, 8557};

	private static final int[] ALL_OBJECT_IDS;

	private static final int SETTING_SUPPLIES_EXTRA = 1611;
	private static final int SETTING_SUPPLIES = 29;

	private static final String FAIRY_TALE_I = "Fairy Tale I - Growing Pains";
	private static final String FAIRY_TALE_III = "Fairy Tale III - Battle at Orks Rift";

	private static final int REGION_MIN_X = 32;
	private static final int REGION_MAX_X = 61;
	private static final int REGION_MIN_Y = 39;
	private static final int REGION_MAX_Y = 64;

	private final HashMap<Integer, Tile> objectToTile = new HashMap<Integer, Tile>();
	private final Set<Integer> searched = new HashSet<Integer>();

	private static Integer[] toInteger(int... ints) {
		Integer[] result = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++) {
			result[i] = ints[i];
		}
		return result;
	}

	static {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Collections.addAll(list, toInteger(HERB));
		Collections.addAll(list, toInteger(BELLADONNA));
		Collections.addAll(list, toInteger(BUSH));
		Collections.addAll(list, toInteger(CACTUS));
		Collections.addAll(list, toInteger(CALQUAT));
		Collections.addAll(list, toInteger(COMPOST));
		Collections.addAll(list, toInteger(FLOWER));
		Collections.addAll(list, toInteger(FRUIT_TREE));
		Collections.addAll(list, toInteger(HOPS));
		Collections.addAll(list, toInteger(MUSHROOM));
		Collections.addAll(list, toInteger(SPIRIT_TREE));
		Collections.addAll(list, toInteger(TREE));
		Collections.addAll(list, toInteger(ALLOTMENT));

		ALL_OBJECT_IDS = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ALL_OBJECT_IDS[i] = list.get(i);
		}
	}

	public static String pretty(String string) {
		return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase().replace('_', ' ');
	}

	public IFarming(final IClientContext ctx) {
		super(ctx);
	}

	public int buckets() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES, 9, 0x1f) + 32 * ctx.varpbits.varpbit(SETTING_SUPPLIES_EXTRA, 17, 0x7);
	}

	public boolean canUseMagicSecateurs() {
		return ctx.definitions.quest(FAIRY_TALE_I).status(ctx) == QuestDefinition.QuestStatus.COMPLETED;
	}

	public boolean canUseMagicWateringCan() {
		return ctx.definitions.quest(FAIRY_TALE_III).status(ctx) == QuestDefinition.QuestStatus.COMPLETED;
	}

	public int compost() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES, 14, 0xff);
	}

	public FarmingDefinition definition(int id) {
		ObjectDefinition def = ctx.definitions.object(id);
		if (def == null) {
			return FarmingDefinition.NIL;
		}

		return new FarmingDefinition(def);
	}

	public MobileIdNameQuery<Herb> herbs() {
		return new MobileIdNameQuery<Herb>(IFarming.this.ctx) {
			@Override
			protected List<Herb> get() {
				ArrayList<Herb> list = new ArrayList<Herb>();
				for (HerbEnum herbEnum : HerbEnum.values()) {
					list.add(herbEnum.object(IFarming.this.ctx));
				}
				return list;
			}

			@Override
			public Herb nil() {
				return new Herb(IFarming.this.ctx, null);
			}
		};
	}

	private final Object locateLock = new Object();

	/**
	 * Search RS surface for location of object
	 *
	 * @param id
	 * @return
	 */
	public Tile locate(int id) {
		// TODO: fire this off on a separate thread
		if (!objectToTile.containsKey(id)) {
			synchronized (locateLock) {
				if (!objectToTile.containsKey(id)) {
					HashSet<Integer> searchingFor = new HashSet<Integer>();
					Collections.addAll(searchingFor, toInteger(ALL_OBJECT_IDS));
					for (Integer integer : objectToTile.keySet()) {
						searchingFor.remove(integer);
					}

					LocalObjectLoader localObjectLoader = ctx.definitions.system().localObjectLoader;


					boolean found = false;
					for (int x = REGION_MIN_X; x <= REGION_MAX_X; x++) {
						for (int y = REGION_MIN_Y; y <= REGION_MAX_Y; y++) {
							int hash = x | y << 7;
							if (searched.contains(hash)) {
								continue;
							}
							searched.add(hash);

							int offsetX = x * 64;
							int offsetY = y * 64;

							if (localObjectLoader.canLoad(hash)) {
								LocalObjects localObjects = localObjectLoader.load(hash);
								List<LocalObject> objects = localObjects.getObjects();
								Collections.shuffle(objects); // Shuffle because I don't want the same tile each time
								for (LocalObject object : objects) {
									if (searchingFor.contains(object.id)) {
										objectToTile.put(object.id, new Tile(offsetX + object.x, offsetY + object.y, object.plane));
										searchingFor.remove(object.id);

										if (object.id == id) {
											found = true;
										}
									}
								}

								if (found) {
									return objectToTile.get(id);
								}
							}
						}
					}

					return Tile.NIL;
				}
			}
		}

		return objectToTile.get(id);
	}

	public int plantCure() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES_EXTRA, 20, 0xff);
	}

	public int superCompost() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES, 22, 0xff);
	}

	/**
	 * @return count of remaining watering can actions on watering can stored in the tool leprechaun
	 * if the watering can is type {@link IFarming.WateringCan#MAGIC} this always returns <i>1</i>
	 */
	public int wateringCan() {
		final WateringCan type = wateringCanType();
		switch (type) {
			case NONE:
				return 0;
			case NORMAL:
				return wateringCanSetting() - 1;
			default:
				return 1;
		}
	}

	private int wateringCanSetting() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES, 4, 0xf);
	}

	/**
	 * Get the type of watering can stored in the tool leprechaun
	 *
	 * @return
	 */
	public WateringCan wateringCanType() {
		final int i = wateringCanSetting();
		if (i == 0) {
			return WateringCan.NONE;
		}
		if (i < 10) {
			return WateringCan.NORMAL;
		}
		return WateringCan.MAGIC;
	}

	enum WateringCan {
		NONE, NORMAL, MAGIC
	}
}
