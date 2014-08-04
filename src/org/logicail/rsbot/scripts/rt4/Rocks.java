package org.logicail.rsbot.scripts.rt4;

import com.logicail.loader.rt4.wrappers.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
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
	TIN(new int[]{24}, new int[]{53}),
	COPPER(new int[]{24}, new int[]{4510});

	private final int[] original;
	private final int[] target;

	// short[][] eventually
	Rocks(int[] original, int[] target) {
		this.original = original;
		this.target = target;
	}

	public Filter<GameObject> filter(final RT4ClientContext ctx) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject gameObject) {
				final ObjectDefinition definition = ctx.definitions.get(gameObject);
				return definition != null && definition.animationId == -1 && Arrays.equals(original, definition.originalColors) && Arrays.equals(target, definition.modifiedColors);
			}
		};
	}
}
