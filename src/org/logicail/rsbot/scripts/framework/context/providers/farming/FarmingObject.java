package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 16:41
 */
public class FarmingObject extends IClientAccessor implements Locatable, Identifiable {
	protected final int setting;
	protected final int shift;
	protected final int mask;
	protected final int object;
	protected final Tile tile;

	public FarmingObject(IClientContext ctx, FarmingObject object) {
		super(ctx);
		this.setting = object.setting;
		this.shift = object.shift;
		this.mask = object.mask;
		this.object = object.object;
		this.tile = object.tile;
	}

	public FarmingObject(IClientContext ctx, int object, int setting, int shift, int mask, Tile tile) {
		super(ctx);
		this.setting = setting;
		this.shift = shift;
		this.mask = mask;
		this.object = object;
		this.tile = tile;
	}

	public FarmingDefinition definition() {
		return ctx.farming.definition(object, bits());
	}

	public int bits() {
		return ctx.varpbits.varpbit(setting, shift, mask);
	}

	@Override
	public int id() {
		return object;
	}

	@Override
	public Tile tile() {
		return tile;
	}
}
