package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.HerbEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject.Herb;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.QuestDefinition;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final String URL_FARMING_JSON = "http://logicail.co.uk/resources/farming.json";

	private static final int SETTING_SUPPLIES_EXTRA = 1611;
	private static final int SETTING_SUPPLIES = 29;

	private static final String FAIRY_TALE_I = "Fairy Tale I - Growing Pains";
	private static final String FAIRY_TALE_III = "Fairy Tale III - Battle at Orks Rift";

	private final Map<Integer, FarmingDefinition> cache = new HashMap<Integer, FarmingDefinition>();
	private final Map<Integer, FarmingDynamicDefinition> dynamicObjects = new HashMap<Integer, FarmingDynamicDefinition>();
	private final Map<String, QuestDefinition> quests = new HashMap<String, QuestDefinition>();

	private volatile boolean initialised = false;
	private final Object lock = new Object();

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
		ensureInitialised();
		return quests.get(FAIRY_TALE_I).complete(ctx);
	}

	private void ensureInitialised() {
		if (!initialised) {
			synchronized (lock) {
				if (!initialised) {
					initialise();
					initialised = true;
				}
			}
		}
	}

	private void initialise() {
		try {
			final String json = ctx.controller.script().downloadString(URL_FARMING_JSON);
			JsonObject map = JsonObject.readFrom(json);

			for (JsonObject.Member member : map.get("quests").asObject()) {
				final JsonObject object = member.getValue().asObject();
				final Config config = new Config(object.get("config").asObject());
				QuestDefinition questDefinition = new QuestDefinition(member.getName(), config, object.get("start").asInt(), object.get("end").asInt());
				quests.put(questDefinition.name, questDefinition);
			}

			for (JsonObject.Member member : map.get("definitions").asObject()) {
				final FarmingDefinition definition = new FarmingDefinition(member);
				cache.put(definition.id(), definition);
			}

			for (JsonObject.Member member : map.get("objects").asObject()) {
				final JsonObject object = member.getValue().asObject();
				final JsonArray ids = object.get("ids").asArray();
				int[] definitions = new int[ids.size()];
				int i = 0;
				for (JsonValue value : ids) {
					definitions[i] = value.asInt();
					i++;
				}

				final Config config = new Config(object.get("config").asObject());
				final JsonObject tile = object.get("tile").asObject();
				Tile t = new Tile(tile.get("x").asInt(), tile.get("y").asInt(), tile.get("z") != null ? tile.get("z").asInt() : 0);
				dynamicObjects.put(Integer.parseInt(member.getName()), new FarmingDynamicDefinition(Integer.parseInt(member.getName()), config, t, definitions));
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.controller.script().log.info("Failed to download/load json data");
			Condition.sleep(2000); // I add a log handler so that the log shows on the screen, so give the user chance to read it
			ctx.controller.stop();
		}
	}

	public boolean canUseMagicWateringCan() {
		ensureInitialised();
		return quests.get(FAIRY_TALE_III).complete(ctx);
	}

	public int compost() {
		return ctx.varpbits.varpbit(SETTING_SUPPLIES, 14, 0xff);
	}

	public FarmingDefinition definition(int id) {
		ensureInitialised();
		final FarmingDefinition definition = cache.get(id);
		return definition != null ? definition : FarmingDefinition.NIL;
	}

	public FarmingDynamicDefinition dynamic(int id) {
		ensureInitialised();
		final FarmingDynamicDefinition object = dynamicObjects.get(id);
		if (object == null) {
			throw new IllegalArgumentException("Don't have id " + id);
		}
		return object;
	}

	public FarmingQuery<Herb> herbs() {
		return new FarmingQuery<Herb>(ctx) {
			@Override
			protected List<Herb> get() {
				ArrayList<Herb> list = new ArrayList<Herb>();
				for (HerbEnum herbEnum : HerbEnum.values()) {
					list.add(herbEnum.object(ctx));
				}
				return list;
			}

			@Override
			public Herb nil() {
				return new Herb(ctx, null);
			}
		};
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