package org.logicail.rsbot.scripts.framework.context.providers.farming;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:47
 */
public class FarmingDefinition {
	public static final FarmingDefinition NIL = new FarmingDefinition(-1, "", null);

	protected final int id;
	protected final String name;
	protected final Set<String> actions = new LinkedHashSet<String>();
	protected final Set<Integer> models = new LinkedHashSet<Integer>();

	FarmingDefinition(JsonObject.Member member) {
		this.id = Integer.parseInt(member.getName());

		final JsonObject value = member.getValue().asObject();

		this.name = value.get("name").isNull() ? "null" : value.get("name").asString();

		final JsonArray modelArray = value.get("models") != null ? value.get("models").asArray() : new JsonArray();
		for (JsonValue v : modelArray) {
			models.add(v.asInt());
		}

		final JsonArray actionArray = value.get("actions") != null ? value.get("actions").asArray() : new JsonArray();
		for (JsonValue v : actionArray) {
			actions.add(v.asString());
		}
	}

	public FarmingDefinition(int id, String name, Iterable<Integer> models) {
		this.id = id;
		this.name = name;
		if (models != null) {
			for (int model : models) {
				this.models.add(model);
			}
		}
	}

	public Set<String> actions() {
		return actions;
	}

	public boolean containsAction(String action) {
		return actions.contains(action);
	}

	public boolean containsModel(int id) {
		return models.contains(id);
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}
}
