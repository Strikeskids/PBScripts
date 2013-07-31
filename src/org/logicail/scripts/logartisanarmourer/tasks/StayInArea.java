package org.logicail.scripts.logartisanarmourer.tasks;

import org.logicail.api.methods.LogicailMethodContext;
import org.logicail.api.methods.providers.Condition;
import org.logicail.framework.script.state.Node;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourerOptions;
import org.logicail.scripts.logartisanarmourer.wrapper.Mode;
import org.powerbot.script.methods.Game;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 25/07/13
 * Time: 19:44
 */
public class StayInArea extends Node {
	public static final Area WORKSHOP_BURIAL = new Area(new Tile(3050, 3345, 0), new Tile(3062, 3333, 0));
	public static final Area WORKSHOP_SWORDS = new Area(new Tile(3034, 3346, 0), new Tile(3049, 3332, 0));
	public static final Area WORKSHOP_TRACKS = new Area(new Tile(3054, 9719, 0), new Tile(3080, 9704, 0));
	public static final Area WORKSHOP_LARGE = new Area(new Tile(3040, 3344, 0), new Tile(3061, 3333, 0));
	public static Area area = null;
	private final LogArtisanArmourerOptions options;

	public StayInArea(LogicailMethodContext ctx) {
		super(ctx);
		this.options = ((LogArtisanArmourer) ctx.script).options;
		switch (options.mode) {
			case BURIAL_ARMOUR:
				area = WORKSHOP_BURIAL;
				break;
			case CEREMONIAL_SWORDS:
				area = WORKSHOP_SWORDS;
				break;
			case REPAIR_TRACK:
				area = WORKSHOP_TRACKS;
				break;
		}
	}

	@Override
	public boolean activate() {
		return ctx.game.getClientState() == Game.INDEX_MAP_LOADED
				&& !area.contains(ctx.players.local());
	}

	@Override
	public void execute() {

		if (options.mode == Mode.BURIAL_ARMOUR) {
			for (GameObject tunnel : ctx.objects.select().id(4618).nearest().first()) {
				if (ctx.interaction.interact(tunnel, "Climb")) {
					ctx.waiting.wait(2000, new Condition() {
						@Override
						public boolean validate() {
							return ctx.game.getClientState() == Game.INDEX_MAP_LOADED
									&& area.contains(ctx.players.local());
						}
					});
					sleep(500, 1500);
				}
			}
		}

		final Tile tile = area.getCentralTile().randomize(3, 3);
		if (ctx.movement.findPath(tile).traverse() || ctx.movement.stepTowards(tile)) {
			ctx.waiting.wait(1500, new Condition() {
				@Override
				public boolean validate() {
					return ctx.movement.getDistance(tile) < 4;
				}
			});
		}

		//if (Calculations.distanceTo(LogArtisanArmourer.getAreaSmall().getCentralTile()) > 100) {
		//	LogArtisanArmourer.get().getLogHandler().print("Too far from Artisan Workshop");
		//}
	}
}
