package org.logicail.rsbot.scripts.framework.context.providers.farming;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.util.IOUtil;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.IdQuery;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:31
 */
public class IFarming extends ClientAccessor {
	private static final String URL_HERBS = "http://logicail.co.uk/resources/farming.json";

	// just 7794 == empty
	// always [x?, 7766]
	public static final int[] MODEL_IDS_WEEDS = {7801, 7800, 7798}; // 3, 2, 1
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875}; // stage 1 to 5 [grown]

	// just 19148, 19152 == empty
	// always have [19148, 19152, x?]
	public static final int[] MODEL_IDS_WEEDS_TROLLHEIM = {19147, 19146, 19145}; // 3, 2, 1, [empty only has 19148, 19152 "Herb patch"]
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};
	public static final int SETTING_HERB = 23;
	public static final int SETTING_HERB_TROLLHEIM = 25;

	private AtomicBoolean ready = new AtomicBoolean();

	private Map<Integer, FarmingDefinition[]> patches = new HashMap<Integer, FarmingDefinition[]>();

	public IFarming(final IClientContext ctx) {
		super(ctx);

		ctx.controller().offer(new Runnable() {
			@Override
			public void run() {
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

					Map<Integer, FarmingDefinition> children = new HashMap<Integer, FarmingDefinition>();

					for (JsonObject.Member member : map.get("definitions").asObject()) {
						final FarmingDefinition definition = new FarmingDefinition(member);
						children.put(definition.id(), definition);
					}

					for (JsonObject.Member member : map.get("herbpatches").asObject()) {
						final JsonArray values = member.getValue().asArray();
						FarmingDefinition[] definitions = new FarmingDefinition[values.size()];
						int i = 0;
						for (JsonValue value : values) {
							final FarmingDefinition definition = children.get(value.asInt());
							definitions[i] = definition;
							i++;
						}
						patches.put(Integer.parseInt(member.getName()), definitions);
					}

					ready.set(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public boolean ready() {
		return ready.get();
	}

	public IdQuery<HerbPatch> herbs() {
		return new IdQuery<HerbPatch>(ctx) {
			@Override
			protected List<HerbPatch> get() {
				return Arrays.asList(HerbPatch.values());
			}

			@Override
			public HerbPatch nil() {
				return null;
			}
		};
	}

	public FarmingDefinition definition(int objectId, int index) {
		final FarmingDefinition definition = patches.get(objectId)[index];
		return definition != null ? definition : FarmingDefinition.NIL;
	}
}
