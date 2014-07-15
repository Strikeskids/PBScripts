package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingHelper;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.enums.HerbEnum;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IGrowthStage;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IWeeds;
import org.powerbot.script.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 17:18
 */
public class Herb extends FarmingObject<Herb.HerbType, HerbEnum> implements IGrowthStage, IWeeds, ICanDie {
	public static final int[] MODEL_IDS_GROWTH_STAGE = {7871, 7872, 7873, 7874, 7875};
	public static final int[] MODEL_IDS_GROWTH_STAGE_TROLLHEIM = {19144, 19150, 19143, 19149, 19140};

	private static Filter<Integer> filter(final int start, final int end) {
		return new Filter<Integer>() {
			@Override
			public boolean accept(Integer i) {
				return i >= start && i <= end;
			}
		};
	}

	public Herb(IClientContext ctx, HerbEnum patch) {
		super(ctx, patch);
	}

	@Override
	public boolean diseased() {
		return FarmingHelper.diseased(this);
	}

	/**
	 * Can the patch be picked
	 *
	 * @return <tt>true</tt> if the patch has finished growing and can be picked, otherwise <tt>false</tt>
	 */
	@Override
	public boolean grown() {
		return definition().containsAction("Pick");
	}

	/**
	 * Growth stage of patch
	 *
	 * @return 0 (empty) to 5 (grown)
	 */
	@Override
	public int stage() {
		final FarmingDefinition definition = definition();

		int[] model_ids_growth_stage = this.id() == HerbEnum.TROLLHEIM.id() ? MODEL_IDS_GROWTH_STAGE_TROLLHEIM : MODEL_IDS_GROWTH_STAGE;
		for (int i = 0; i < model_ids_growth_stage.length; i++) {
			if (definition.containsModel(model_ids_growth_stage[i])) {
				return i + 1;
			}
		}

		return 0;
	}

	@Override
	public HerbType type() {
		if (empty()) {
			return HerbType.HERB_PATCH;
		}

		if (dead()) {
			return HerbType.DEAD;
		}

		final int bits = bits();
		for (HerbType type : HerbType.values()) {
			if (type.valid(bits)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknown herb type! bits:" + bits);
	}

	@Override
	public boolean dead() {
		return FarmingHelper.dead(this);
	}

	@Override
	public int weeds() {
		return FarmingHelper.weeds(this);
	}

	public enum HerbType {
		HERB_PATCH(),
		DEAD(),
		GUAM(4, 10, 128, 130),
		MARRENTIL(11, 17, 131, 133),
		TARROMIN(18, 24, 134, 136),
		HARRALANDER(25, 31, 137, 139),
		RANARR(32, 38, 140, 142),
		TOADFLAX(39, 45, 143, 145),
		IRIT(46, 52, 146, 148),
		AVANTOE(53, 59, 149, 151),
		WERGALI(60, 66, 152, 154),
		KWUARM(68, 74, 155, 157),
		SNAPDRAGON(75, 81, 158, 160),
		CADANTINE(82, 88, 161, 163),
		LANTADYME(89, 95, 164, 166),
		DWARF_WEED(96, 102, 167, 169),
		TORSTOL(103, 109, 173, 175),
		FELLSTALK(new Filter<Integer>() {
			@Override
			public boolean accept(Integer i) {
				return i == 67 || (i >= 110 && i <= 115);
			}
		}, 176, 178),
		GOUTWEED(192, 203),
		SPIRIT_WEED(204, 213);

		private final Filter<Integer> filterGrowing;
		private final Filter<Integer> diseasedFilter;

		public boolean valid(int i) {
			return (filterGrowing != null && filterGrowing.accept(i))
					|| (diseasedFilter != null && diseasedFilter.accept(i));
		}

		HerbType() {
			filterGrowing = null;
			diseasedFilter = null;
		}

		HerbType(int start, int end) {
			filterGrowing = filter(start, end);
			diseasedFilter = null;
		}

		HerbType(int start, int end, int diseasedStart, int diseasedEnd) {
			this(filter(start, end), diseasedStart, diseasedEnd);
		}

		HerbType(Filter<Integer> filterGrowing, int diseasedStart, int diseasedEnd) {
			this.filterGrowing = filterGrowing;
			diseasedFilter = filter(diseasedStart, diseasedEnd);
		}
	}
}
