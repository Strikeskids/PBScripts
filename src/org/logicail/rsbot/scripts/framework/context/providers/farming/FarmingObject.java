package org.logicail.rsbot.scripts.framework.context.providers.farming;

import org.logicail.rsbot.scripts.framework.context.IClientAccessor;
import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanDie;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.ICanWater;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IClearable;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IWeeds;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 14/04/2014
 * Time: 16:41
 */
public abstract class FarmingObject<T extends Enum, E extends Enum<E> & Identifiable> extends IClientAccessor implements Locatable, Identifiable, IClearable, Nameable {
	public final E parent;
	protected final int setting;
	protected final int shift;
	protected final int mask;
	protected final int object;
	protected final Tile tile;
	protected final int[] children;

	@Override
	public String name() {
		return definition().name();
	}

	public FarmingObject(IClientContext ctx, E enumType) {
		super(ctx);
		parent = enumType;
		final FarmingDynamicDefinition dynamic = ctx.farming().dynamic(enumType.id());
		this.setting = dynamic.setting;
		this.shift = dynamic.shift;
		this.mask = dynamic.mask;
		this.object = dynamic.object;
		this.tile = dynamic.tile;
		this.children = dynamic.children;
	}

	@Override
	public boolean clearable() {
		return definition().containsAction("Clear");
	}

	public FarmingDefinition definition() {
		return ctx.farming().definition(children[bits()]);
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
	 * @return <tt>true</tt> if patch has nothing growing on it, otherwise <tt>false</tt>
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
