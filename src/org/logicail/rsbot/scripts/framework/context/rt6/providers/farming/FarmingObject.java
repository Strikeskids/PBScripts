package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import org.logicail.rsbot.scripts.framework.context.rt6.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IClearable;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.farming.interfaces.IWeeds;
import org.powerbot.script.*;
import org.powerbot.script.rt6.GameObject;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 16:41
 */
public abstract class FarmingObject<T extends Enum, E extends Enum<E> & Identifiable> extends IClientAccessor implements Locatable, Identifiable, IClearable, Nameable, Validatable {
	public final E parent;
	protected final int setting;
	protected final int shift;
	protected final int mask;
	protected final int object;
	protected final Tile tile;
	protected final int[] children;

	public FarmingObject(IClientContext ctx, E enumType) {
		super(ctx);
		parent = enumType;

		if (enumType != null) {
			final FarmingDynamicDefinition dynamic = ctx.farming.dynamic(enumType.id());
			this.setting = dynamic.setting;
			this.shift = dynamic.shift;
			this.mask = dynamic.mask;
			this.object = dynamic.object;
			this.tile = dynamic.tile;
			this.children = dynamic.children;
		} else {
			// NIL
			setting = 0;
			shift = 0;
			mask = 0;
			object = -1;
			tile = Tile.NIL;
			children = new int[0];
		}
	}

	@Override
	public boolean clearable() {
		return definition().containsAction("Clear");
	}

	@Override
	public int id() {
		return object;
	}

	@Override
	public String name() {
		return definition().name();
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
	 * @return <tt>true</tt> if patch has nothing growing on it, otherwise <tt>false</tt>
	 */
	public boolean empty() {
		return bits() <= 3;
	}

	protected abstract boolean grown();

	@Override
	public Tile tile() {
		if (valid()) {
			final GameObject nearest = ctx.objects.select().id(id()).nearest().poll();
			if (nearest.valid()) {
				return nearest.tile();
			}
		}
		return tile;
	}

	@Override
	public boolean valid() {
		return definition() != FarmingDefinition.NIL;
	}

	public FarmingDefinition definition() {
		final int bits = bits();
		if (children.length == 0 || bits > children.length) {
			return FarmingDefinition.NIL;
		}

		return ctx.farming.definition(children[bits]);
	}

	public int bits() {
		return ctx.varpbits.varpbit(setting, shift, mask);
	}

	public abstract T type();
}
