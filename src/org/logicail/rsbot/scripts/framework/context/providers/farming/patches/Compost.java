package org.logicail.rsbot.scripts.framework.context.providers.farming.patches;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingDefinition;
import org.logicail.rsbot.scripts.framework.context.providers.farming.FarmingObject;
import org.logicail.rsbot.scripts.framework.context.providers.farming.enums.CompostEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 13/04/2014
 * Time: 12:25
 */
public class Compost extends FarmingObject {
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

	public boolean grown() {
		final FarmingDefinition definition = definition();
		if (definition.containsAction("Empty") || definition.containsAction("Take-tomato")) {
			return true;
		}

		final int bits = bits();
		return bits == NORMAL_STAGE[2] || bits == SUPER_STAGE[2] || bits == TOMATO_STAGE[2];
	}

	private int stage() {
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

		return -1;
	}

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

	public boolean empty() {
		return bits() == 0;
	}

	enum CompostType {
		NORMAL, SUPER, TOMATO, EMPTY
	}
}
