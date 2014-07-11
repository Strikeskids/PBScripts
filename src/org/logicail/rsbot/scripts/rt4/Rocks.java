package org.logicail.rsbot.scripts.rt4;

import com.logicail.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.GameObject;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 19:05
 */
public enum Rocks {
	// Animation ID 531 is gas random
	TIN(new short[]{24}, new short[]{53}),
	COPPER(new short[]{24}, new short[]{4510});

	private final short[] original;
	private final short[] target;

	// short[][] eventually
	Rocks(short[] original, short[] target) {
		this.original = original;
		this.target = target;
	}

	public Filter<GameObject> filter(final IClientContext ctx) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				final ObjectDefinition definition = ctx.definitions.get(gameObject);
				return definition != null && definition.animationId == -1 && Arrays.equals(original, definition.recolorOriginal) && Arrays.equals(target, definition.recolorTarget);
			}
		};
	}
}
