package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;
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
public abstract class FarmingObject<T extends Enum> extends IClientAccessor implements Locatable, Identifiable {
	protected final int setting;
	protected final int shift;
	protected final int mask;
	protected final int object;
	protected final Tile tile;
	protected final int[] children;

	public FarmingObject(IClientContext context, int id) {
		this(context, context.farming.dynamic(id));
	}

	private FarmingObject(IClientContext ctx, FarmingDynamicDefinition object) {
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

	/**
	 * Can the crop be cleared
	 *
	 * @return
	 */
	public boolean clearable() {
		return definition().containsAction("Clear");
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
		g.setColor(state().color());
		g.fillRect(x, y, 9, 9);
		g.setColor(Color.gray);
		g.drawRect(x, y, 9, 9);
	}

	public CropState state() {
		if (this instanceof IWeeds) {
			if (((IWeeds) this).weeds() > 0) {
				return CropState.WEEDS;
			}
		}

		if (empty()) {
			return CropState.EMPTY;
		}

		if (this instanceof ICanDie) {
			ICanDie canDie = (ICanDie) this;

			if (canDie.diseased()) {
				return CropState.DISEASED;
			}

			if (canDie.dead()) {
				return CropState.DEAD;
			}
		}

		if (grown()) {
			return CropState.READY;
		}

		if (this instanceof ICanWater) {
			if (((ICanWater) this).watered()) {
				return CropState.WATERED;
			}
		}

		return CropState.GROWING;
	}

	/**
	 * Is the patch empty of any crop
	 *
	 * @return <tt>true</tt> if patch has no herb growing, otherwise <tt>false</tt>
	 */
	public boolean empty() {
		return bits() <= 3;
	}

	protected abstract boolean grown();

	@Override
	public Tile tile() {
		return tile;
	}

	public abstract T type();
}
