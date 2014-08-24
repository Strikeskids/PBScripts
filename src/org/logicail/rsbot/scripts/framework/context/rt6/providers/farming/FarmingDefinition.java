package org.logicail.rsbot.scripts.framework.context.rt6.providers.farming;

import org.logicail.cache.loader.rt6.wrapper.ObjectDefinition;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:47
 */
public class FarmingDefinition {
	public static final FarmingDefinition NIL = new FarmingDefinition(null);

	private ObjectDefinition definition;

	public FarmingDefinition(ObjectDefinition definition) {
		this.definition = definition;
	}

	public boolean containsAction(String action) {
		if (definition == null) {
			return false;
		}

		String[] actions = definition.actions;
		if (action != null) {
			for (String s : actions) {
				if (s != null && s.equalsIgnoreCase(action)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean containsModel(int id) {
		if (definition == null) {
			return false;
		}

		int[][] models = definition.modelIds;
		if (models != null) {
			int[] ints = models[0];
			for (int i : ints) {
				if (i == id) {
					return true;
				}
			}
		}
		return false;
	}

	public int id() {
		return definition == null ? -1 : definition.id;
	}

	public String name() {
		return definition == null ? "" : definition.name;
	}

	public int childId(IClientContext ctx) {
		return definition == null ? -1 : definition.childId(ctx);
	}

	public FarmingDefinition child(IClientContext ctx) {
		ObjectDefinition def = definition == null ? null : definition.child(ctx);
		if (def == null) {
			return FarmingDefinition.NIL;
		}

		return new FarmingDefinition(def);
	}
}
