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
	private static final Object lock = new Object();
	public final E parent;
	protected final int id;

	public FarmingObject(IClientContext ctx, E enumType) {
		super(ctx);
		parent = enumType;
		id = enumType.id();
	}

	@Override
	public boolean clearable() {
		return definition().containsAction("Clear");
	}

	@Override
	public int id() {
		return id;
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

	public int bits() {
		return definition().childId(ctx);
	}

	protected abstract boolean grown();

	@Override
	public Tile tile() {
		if (valid()) {
			final GameObject nearest = ctx.objects.select().id(id).nearest().poll();
			if (nearest.valid()) {
				return nearest.tile();
			}
		}

		return ctx.farming.locate(id);
	}

	@Override
	public boolean valid() {
		return definition() != FarmingDefinition.NIL;
	}

	public FarmingDefinition definition() {
		return ctx.farming.definition(id);
	}

	public abstract T type();
}
