package org.logicail.rsbot.scripts.framework.context.providers.farming.farmingobject;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.CropState;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.CompostEnum;
import org.logicail.rsbot.scripts.framework.context.providers.farming.interfaces.IGrowthStage;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 12:25
 */
public class Compost extends FarmingObject<Compost.CompostType> implements IGrowthStage {
	private static final int[] NORMAL_STAGE = {31, 32, 94};
	private static final int[] SUPER_STAGE = {95, 96, 126};
	private static final int[] TOMATO_STAGE = {159, 160, 222};

	public Compost(IClientContext ctx, CompostEnum patch) {
		super(ctx, patch.id());
	}

	public int count() {
		if (closed()) {
			return 15; // Unopened
		}

		final int bits = bits();

		int count = bits & 0xf;

		if (((bits >>> 4) & 0x1) == 1) {
			return count + 1;
		}

		return count;
	}

	public boolean closed() {
		return definition().containsModel(7844);
	}

	@Override
	public void repaint(Graphics2D g, int x, int y) {
		if (empty()) {
			g.setColor(CropState.EMPTY.color());
		} else if (grown()) {
			g.setColor(CropState.READY.color());
		} else {
			g.setColor(CropState.GROWING.color());
		}

		g.fillRect(x, y, 5, 5);
		g.setColor(Color.gray);
		g.drawRect(x, y, 5, 5);
	}

	@Override
	public boolean empty() {
		return bits() == 0;
	}

	@Override
	public boolean grown() {
		final FarmingDefinition definition = definition();
		if (definition.containsAction("Empty") || definition.containsAction("Take-tomato")) {
			return true;
		}

		final int bits = bits();
		return bits == NORMAL_STAGE[2] || bits == SUPER_STAGE[2] || bits == TOMATO_STAGE[2];
	}

	@Override
	public int stage() {
		final int bits = bits();

		for (int i = 0; i < NORMAL_STAGE.length; i++) {
			if (bits == NORMAL_STAGE[i]) {
				return i + 1;
			}
		}

		for (int i = 0; i < SUPER_STAGE.length; i++) {
			if (bits == SUPER_STAGE[i]) {
				return i + 1;
			}
		}

		for (int i = 0; i < TOMATO_STAGE.length; i++) {
			if (bits == TOMATO_STAGE[i]) {
				return i + 1;
			}
		}

		return 4;
	}

	@Override
	public CropState state() {
		return CropState.EMPTY;
	}

	@Override
	public CompostType type() {
		if (empty()) {
			return CompostType.EMPTY;
		}

		final int bits = bits();

		if (bits <= 32 || bits == NORMAL_STAGE[2]) {
			return CompostType.NORMAL;
		}

		if (bits < 64 || bits == SUPER_STAGE[0] || bits == SUPER_STAGE[1] || bits == SUPER_STAGE[2]) {
			return CompostType.SUPER;
		}

		return CompostType.TOMATO;
	}

	public enum CompostType {
		NORMAL, SUPER, TOMATO, EMPTY
	}
}
