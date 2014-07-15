package org.logicail.rsbot.scripts.testing;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 23/02/14
 * Time: 20:57
 */
public class Slayer extends IClientAccessor {
	public Slayer(IClientContext context) {
		super(context);
	}

	/**
	 * Get your slayer points
	 *
	 * @return
	 */
	public int getPoints() {
		// [9071] ctx.settings.get(2092, 0, 0x1ffff)
		return ctx.varpbits.varpbit(2092, 0, 0x1ffff);
	}

	/**
	 * Get the number of kills remaining
	 *
	 * @return
	 */
	public int getRemaining() {
		// [7917] ctx.settings.get(183, 0, 0x7f)
		return ctx.varpbits.varpbit(183, 0, 0x7f);
	}

	/**
	 * Get the number of monsters that were assigned
	 *
	 * @return
	 */
	// Doesn't work for low level accounts?
//	public int getAssignedAmount() {
//		// [521] ctx.settings.get(2170, 9, 0x1ff)
//		return ctx.settings.get(2170, 9, 0x1ff);
//	}

	// Only seems to be used for Slayer Skip Lists
	public enum Assignment {
		MONKEYS,
		GOBLINS,
		RATS,
		SPIDERS,
		BIRDS,
		COWS,
		SCORPIONS,
		BATS,
		WOLVES,
		ZOMBIES,
		SKELETONS,
		GHOSTS,
		BEARS,
		HILL_GIANTS,
		ICE_GIANTS,
		FIRE_GIANTS,
		MOSS_GIANTS,
		TROLLS,
		ICE_WARRIORS,
		OGRES,
		HOBGOBLINS,
		DOGS,
		GHOULS,
		GREEN_DRAGONS,
		BLUE_DRAGONS,
		RED_DRAGONS,
		BLACK_DRAGONS,
		LESSER_DEMONS,
		GREATER_DEMONS,
		BLACK_DEMONS,
		HELLHOUNDS,
		SHADOW_WARRIORS,
		WEREWOLVES,
		VAMPYRES,
		DAGANNOTH,
		TUROTH,
		CAVE_CRAWLERS,
		BANSHEES,
		CRAWLING_HANDS,
		INFERNAL_MAGES,
		ABERRANT_SPECTRES,
		ABYSSAL_DEMONS,
		BASILISKS,
		COCKATRICE,
		KURASK,
		GARGOYLES,
		PYREFIENDS,
		BLOODVELD,
		DUST_DEVILS,
		JELLIES,
		ROCKSLUGS,
		NECHRYAEL,
		KALPHITE,
		EARTH_WARRIORS,
		OTHERWORLDLY_BEINGS,
		ELVES,
		DWARVES,
		BRONZE_DRAGONS,
		IRON_DRAGONS,
		STEEL_DRAGONS,
		WALL_BEASTS,
		CAVE_SLIMES,
		CAVE_BUGS,
		SHADES,
		CROCODILES,
		DARK_BEASTS,
		MOGRES,
		DESERT_LIZARDS,
		FEVER_SPIDERS,
		HARPIE_BUG_SWARMS,
		SEA_SNAKES,
		SKELETAL_WYVERNS,
		KILLERWATTS,
		MUTATED_ZYGOMITES,
		ICEFIENDS,
		MINOTAURS,
		FLESHCRAWLERS,
		CATABLEPON,
		ANKOU,
		CAVE_HORRORS,
		JUNGLE_HORRORS,
		GORAKS,
		SUQAHS,
		BRINE_RATS,
		SCABARITES,
		TERROR_DOGS,
		MOLANISKS,
		WATERFIENDS,
		SPIRITUAL_WARRIORS,
		SPIRITUAL_RANGERS,
		SPIRITUAL_MAGES,
		WARPED_TORTOISES,
		WARPED_TERRORBIRDS,
		MITHRIL_DRAGONS,
		AQUANITES,
		GANODERMIC_CREATURES,
		GRIFOLAPINES,
		GRIFOLAROOS,
		FUNGAL_MAGI,
		POLYPORE_CREATURES,
		TZHAAR,
		VOLCANIC_CREATURES,
		JUNGLE_STRYKEWYRMS,
		DESERT_STRYKEWYRMS,
		ICE_STRYKEWYRMS,
		LIVING_ROCK_CREATURES,
		CYCLOPES,
		MUTATED_JADINKOS,
		VYREWATCH,
		GELATINOUS_ABOMINATIONS,
		GROTWORMS,
		CRESS_CREATIONS,
		AVIANSIES,
		ASCENSION_MEMBERS,
		PIGS,
		AIRUT
	}
}
