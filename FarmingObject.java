package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

import java.awt.*;

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
	protected final int[] children;

	public FarmingObject(IClientContext context, int id) {
		this(context, context.farming.dynamic(id));
	}

	private FarmingObject(IClientContext ctx, FarmingObject object) {
		this(ctx, object.object, object.setting, object.shift, object.mask, object.tile, object.children);
	}

	public FarmingObject(IClientContext ctx, int object, int setting, int shift, int mask, Tile tile, int[] children) {
		super(ctx);
		this.setting = setting;
		this.shift = shift;
		this.mask = mask;
		this.object = object;
		this.tile = tile;
		this.children = children;
	}

	public FarmingDefinition definition() {
		return ctx.farming.definition(children[bits()]);
	}

	public int bits() {
		return ctx.varpbits.varpbit(setting, shift, mask);
	}

	@Override
	public int id() {
		return object;
	}

	public void repaint(Graphics2D g, int x, int y) {
	}

	@Override
	public Tile tile() {
		return tile;
	}
}
