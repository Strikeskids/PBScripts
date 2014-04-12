package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 17:32
 */
public enum HerbPatch implements Locatable, Identifiable {
	FALADOR(8150, IFarming.SETTING_HERB, 0, new Tile(3058, 3311)),
	CATHERBY(8151, IFarming.SETTING_HERB, 8, new Tile(2813, 3463)),
	ARDOUGNE(8152, IFarming.SETTING_HERB, 16, new Tile(2670, 3374)),
	PORT_PHASMATYS(8153, IFarming.SETTING_HERB, 24, new Tile(3605, 3529)),
	TROLLHEIM(18816, IFarming.SETTING_HERB_TROLLHEIM, 0, new Tile(2812, 3680));
	private final int id;
	private final Tile tile;
	public final int setting;
	public final int shift;

	HerbPatch(int id, int setting, int shift, Tile location) {
		this.id = id;
		this.setting = setting;
		this.shift = shift;
		this.tile = location;
	}

	public int bits(IClientContext ctx) {
		return ctx.varpbits.varpbit(setting, shift, 0xff);
	}

	private FarmingDefinition definition(IClientContext ctx) {
		return ctx.farming.definition(id, bits(ctx));
	}

	public CropState state(IClientContext ctx) {
		if (diseased(ctx)) {
			return CropState.DISEASED;
		}

		if (dead(ctx)) {
			return CropState.DEAD;
		}

		if (weeds(ctx) > 0) {
			return CropState.WEEDS;
		}

		if (empty(ctx)) {
			return CropState.EMPTY;
		}

		if (grown(ctx)) {
			return CropState.READY;
		}

		return CropState.GROWING;
	}

	public int weeds(IClientContext ctx) {
		switch (bits(ctx)) {
			case 0:
				return 3;
			case 1:
				return 2;
			case 2:
				return 1;
		}

		return 0;
	}

	public int stage(IClientContext ctx) {
		final FarmingDefinition definition = definition(ctx);

		int[] model_ids_growth_stage = this == TROLLHEIM ? IFarming.MODEL_IDS_GROWTH_STAGE_TROLLHEIM : IFarming.MODEL_IDS_GROWTH_STAGE;
		for (int i = 0; i < model_ids_growth_stage.length; i++) {
			int id = model_ids_growth_stage[i];
			for (int model : definition.models) {
				if (model == id) {
					return i + 1;
				}
			}
		}

		return 0;
	}

	public boolean empty(IClientContext ctx) {
		return bits(ctx) == 3;
	}

	public boolean grown(IClientContext ctx) {
		return stage(ctx) >= 5;
	}

	public boolean diseased(IClientContext ctx) {
		return definition(ctx).name.startsWith("Diseased");
	}

	public boolean dead(IClientContext ctx) {
		return definition(ctx).name.startsWith("Dead");
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Tile tile() {
		return tile;
	}
}
