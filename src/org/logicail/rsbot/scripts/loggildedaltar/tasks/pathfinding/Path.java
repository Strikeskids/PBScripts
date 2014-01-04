package org.logicail.rsbot.scripts.loggildedaltar.tasks.pathfinding;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 03/01/14
 * Time: 15:36
 */
public enum Path {
	// Bank
	BURTHORPE_TROLL_INVASION("Burthorpe: Games Necklace", PathType.BANK, LocationAttribute.BURTHORPE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.REQUIRE_FAILSAFE)),
	BURTHORPE_LODESTONE("Burthorpe: Lodestones", PathType.BANK, LocationAttribute.BURTHORPE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	CANIFIS("Portal Chamber: Kharyrll", PathType.BANK, LocationAttribute.CANIFIS, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.REQUIRE_FAILSAFE)),
	CASTLE_WARS_RING_OF_DUELING("Castle Wars: Ring of duelling", PathType.BANK, LocationAttribute.CASTLE_WARS, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.ENABLE_DEFAULT, PathSetting.REQUIRE_FAILSAFE)),
	DAEMONHEIM_RING_OF_KINSHIP("Daemonheim: Ring of kinship", PathType.BANK, LocationAttribute.DAEMONHEIM, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	EDGEVILLE_AMULET_OF_GLORY("Edgeville: Amulet of glory", PathType.BANK, LocationAttribute.EDGEVILLE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.REQUIRE_FAILSAFE)),
	EDGEVILLE_LODESTONE("Edgeville: Lodestones", PathType.BANK, LocationAttribute.EDGEVILLE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.ENABLE_DEFAULT, PathSetting.IS_FAILSAFE)),
	EDGEVILLE_MOUNTED_AMULET_OF_GLORY("Edgeville: Mounted Amulet of glory", PathType.BANK, LocationAttribute.EDGEVILLE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.ENABLE_DEFAULT, PathSetting.REQUIRE_FAILSAFE)),
	FIGHT_CAVES("Fight Caves: TokKul-Zo", PathType.BANK, LocationAttribute.FIGHT_CAVES, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	LUNAR_ISLE_LODESTONE("Lunar Isle: Lodestones", PathType.BANK, LocationAttribute.LUNAR_ISLE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	//VARROCK_TABLET("Varrock: Magic tablet", PathType.BANK, LocationAttribute.VARROCK),
	YANILLE_BANK_WALK("Yanille: Walk to bank", PathType.BANK, LocationAttribute.YANILLE_BANK, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),

	// House
	HOME_TABLET("Magic Tablet", PathType.HOME, LocationAttribute.ANYWHERE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.ENABLE_DEFAULT, PathSetting.IS_FAILSAFE)),
	HOME_TELEPORT("Home Teleport Earth staff, law and air runes", PathType.HOME, LocationAttribute.ANYWHERE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	HOME_TELEPORT_RUNIC_STAFF("Home Teleport (Greater) runic staff", PathType.HOME, LocationAttribute.ANYWHERE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	HOME_PORTAL("Portal", PathType.HOME, LocationAttribute.ANYWHERE, EnumSet.of(PathSetting.IS_FAILSAFE)),
	HOME_YANILLE_WALK("Yanille walk to house", PathType.HOME, LocationAttribute.YANILLE_HOUSE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	TAVERLEY_LODESTONE("Taverley Lodestones", PathType.HOME, LocationAttribute.TAVERLEY_HOUSE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	YANILLE_LODESTONE("Yanille Lodestones", PathType.HOME, LocationAttribute.YANILLE_HOUSE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.IS_FAILSAFE)),
	YANILLE_TABLET("Yanille tablet", PathType.HOME, LocationAttribute.ANYWHERE, EnumSet.of(PathSetting.ENABLE_LIST, PathSetting.ENABLE_DEFAULT, PathSetting.IS_FAILSAFE)),

	// Summoning
	CASTLE_WARS_OBELISK("Castle Wars: Recharge Summoning", PathType.SUMMONING, LocationAttribute.CASTLE_WARS, EnumSet.of(PathSetting.IS_FAILSAFE)),
	YANILLE_OBELISK("Yanille: Recharge Summoning", PathType.SUMMONING, LocationAttribute.YANILLE_BANK, EnumSet.of(PathSetting.IS_FAILSAFE)),
	EDGEVILLE_OBELISK("Edgeville: Recharge Summoning", PathType.SUMMONING, LocationAttribute.EDGEVILLE, EnumSet.of(PathSetting.IS_FAILSAFE)),
	LUNAR_ISLE_OBELISK("Lunar Isle: Recharge Summoning", PathType.SUMMONING, LocationAttribute.LUNAR_ISLE, EnumSet.of(PathSetting.IS_FAILSAFE)),
	BURTHORPE_OBELISK("Burthorpe: Recharge Summoning", PathType.SUMMONING, LocationAttribute.BURTHORPE, EnumSet.of(PathSetting.IS_FAILSAFE)),
	TZHAAR_MAIN_PLAZA_OBELISK("TzHaar: Main plaza Summoning", PathType.SUMMONING, LocationAttribute.TZHAAR_MAIN_PLAZA, EnumSet.of(PathSetting.IS_FAILSAFE)),
	CANIFIS_OBELISK("Canifis: Recharge Summoning", PathType.SUMMONING, LocationAttribute.CANIFIS, EnumSet.of(PathSetting.IS_FAILSAFE));

	private final String name;
	private final LocationAttribute location;
	private final PathType pathType;
	private final EnumSet<PathSetting> options;

	private Path(final String name, PathType pathType, final LocationAttribute location, EnumSet<PathSetting> options) {
		this.name = name;
		this.location = location;
		this.pathType = pathType;
		this.options = options;
	}

	public String getName() {
		return name;
	}

	public LocationAttribute getLocation() {
		return location;
	}

	public boolean isEnabledInList() {
		return options.contains(PathSetting.ENABLE_LIST);
	}

	public boolean isEnabledByDefault() {
		return options.contains(PathSetting.ENABLE_DEFAULT);
	}

	public boolean isFailsafe() {
		return options.contains(PathSetting.IS_FAILSAFE);
	}

	public boolean isFailsafeRequired() {
		return options.contains(PathSetting.REQUIRE_FAILSAFE);
	}

	public PathType getPathType() {
		return pathType;
	}

	@Override
	public String toString() {
		if (isFailsafe()) {
			return String.format("[F] %s", getName());
		}
		return getName();
	}

	public enum PathType {
		BANK,
		HOME,
		SUMMONING
	}

	public enum PathSetting {
		ENABLE_LIST,
		ENABLE_DEFAULT,
		REQUIRE_FAILSAFE,
		IS_FAILSAFE
	}
}
