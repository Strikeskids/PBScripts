package org.logicail.rsbot.scripts.framework.context.providers.farming;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.HerbEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.patches.Herb;
import org.logicail.rsbot.util.IOUtil;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:31
 */
public class IFarming extends IClientAccessor {
	// just 7794 == empty
	// always [x?, 7766]
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875}; // stage 1 to 5 [grown]

	// just 19148, 19152 == empty
	// always have [19148, 19152, x?]
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};
	public static final int SETTING_HERB = 23;
	public static final int SETTING_HERB_TROLLHEIM = 25;

	public static final int[] HERB = {8150, 8151, 8152, 8153, 18816};
	public static final int BELLADONNA = 7572;
	public static final int[] BUSH = {7577, 7578, 7579, 7580};
	public static final int CACTUS = 7771;
	public static final int CALQUAT = 7807;
	public static final int[] COMPOST = {7836, 7837, 7838, 7839};
	public static final int[] FLOWER = {7847, 7848, 7849, 7850};
	public static final int[] FRUIT_TREE = {7962, 7963, 7964, 7965};
	public static final int[] HOPS = {8173, 8174, 8175, 8176};
	public static final int MUSHROOM = 8337;
	public static final int[] SPIRIT_TREE = {8338, 8382, 8383};
	public static final int[] TREE = {8388, 8389, 8390, 8391};
	public static final int[] ALLOTMENT = {8550, 8551, 8552, 8553, 8554, 8555, 8556, 8557};
	private static final String URL_HERBS = "http://logicail.co.uk/resources/farming.json";

	private final Map<Integer, FarmingDefinition> cache = new HashMap<Integer, FarmingDefinition>();
	private final Map<Integer, Integer[]> objects = new HashMap<Integer, Integer[]>();
	private final Map<Integer, FarmingObject> dynamicObjects = new HashMap<Integer, FarmingObject>();

	public IFarming(final IClientContext ctx) {
		super(ctx);
		final File file = ctx.script.download(URL_HERBS, "farming.json");
		try {
			if (!file.exists()) {
				ctx.script.log.info("Failed to download json data");
				Condition.sleep(2000);
				ctx.controller().stop();
				return;
			}
			final DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

			JsonObject map = JsonObject.readFrom(IOUtil.read(stream));

			for (JsonObject.Member member : map.get("definitions").asObject()) {
				final FarmingDefinition definition = new FarmingDefinition(member);
				cache.put(definition.id(), definition);
			}

			for (JsonObject.Member member : map.get("objects").asObject()) {
				final JsonObject object = member.getValue().asObject();
				final JsonArray ids = object.get("ids").asArray();
				Integer[] definitions = new Integer[ids.size()];
				int i = 0;
				for (JsonValue value : ids) {
					definitions[i] = value.asInt();
					i++;
				}
				objects.put(Integer.parseInt(member.getName()), definitions);

				final JsonObject config = object.get("config").asObject();
				final JsonObject tile = object.get("tile").asObject();
				Tile t = new Tile(tile.get("x").asInt(), tile.get("y").asInt(), tile.get("z") != null ? tile.get("z").asInt() : 0);
				dynamicObjects.put(Integer.parseInt(member.getName()), new FarmingObject(ctx, Integer.parseInt(member.getName()), config.get("id").asInt(), config.get("shift").asInt(), config.get("mask").asInt(), t));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FarmingDefinition definition(int objectId, int index) {
		final FarmingDefinition definition = cache.get(objects.get(objectId)[index]);
		return definition != null ? definition : FarmingDefinition.NIL;
	}

	public FarmingObject dynamic(int id) {
		return dynamicObjects.get(id);
	}

	public FarmingQuery<Herb> herbs(final EnumSet<HerbEnum> enums) {
		return new FarmingQuery<Herb>(ctx) {
			@Override
			protected List<Herb> get() {
				ArrayList<Herb> list = new ArrayList<Herb>(enums.size());
				for (HerbEnum herbEnum : enums) {
					list.add(herbEnum.herb(ctx));
				}
				return list;
			}

			@Override
			public Herb nil() {
				return null;
			}
		};
	}
}
