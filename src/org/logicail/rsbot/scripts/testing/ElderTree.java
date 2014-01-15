package org.logicail.rsbot.scripts.testing;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/01/14
 * Time: 11:51
 */
public enum ElderTree {
	// TODO: Need to map location of tree to enum
	PISCATORIS(3881, 0, 3881, 12, new Tile(2321, 3599, 0)),
	TREE_GNOME_STRONGHOLD(3881, 1, 3881, 16, new Tile(2426, 3453, 0)),
	YANILLE(3881, 2, 3881, 20, new Tile(2572, 3058, 0)),
	LLETYA(3881, 3, 3881, 24, new Tile(2296, 3145, 0)),
	SORCERERS_TOWER(3881, 4, 3881, 28, new Tile(2735, 3402, 0)),
	EDGEVILLE(3881, 5, 3882, 0, new Tile(3091, 3452, 0)),
	FALADOR_FARM(3881, 6, 3882, 4, new Tile(3054, 3317, 0)),
	RIMMINGTON(3881, 7, 3882, 8, new Tile(2929, 3228, 0)),
	DRAYNOR_VILLAGE(3881, 8, 3882, 12, new Tile(3095, 3213, 0)),
	VARROCK(3881, 9, 3882, 16, new Tile(3260, 3368, 0));
	private final int settingAvailable;
	private final int activeShift;
	private final int settingTime;
	private final int timeShift;
	private final Tile location;

	ElderTree(int settingAvailable, int availableShift, int settingTime, int timeShift, Tile location) {
		this.settingAvailable = settingAvailable;
		this.activeShift = availableShift;
		this.settingTime = settingTime;
		this.timeShift = timeShift;
		this.location = location;
	}

	/**
	 * Can the tree be cut
	 *
	 * @param ctx
	 * @return <tt>true</tt> if tree can be cut
	 */
	public boolean hasBranches(MethodContext ctx) {
		return ctx.settings.get(settingAvailable, activeShift, 1) == 0;
	}

	/**
	 * Get tree timer in minutes
	 *
	 * @param ctx
	 * @return Minutes
	 * if #hasBranches is <tt>true</tt>
	 * <tt>0</tt> if have not started chopping
	 * <tt>1-5</tt> minutes of tree life remaining
	 * else
	 * <tt>1-10</tt> minutes until tree is available again
	 * @see #hasBranches
	 */
	public int getTime(MethodContext ctx) {
		return ctx.settings.get(settingTime, timeShift, 0xf);
	}

	public Tile getLocation() {
		return location;
	}
}
