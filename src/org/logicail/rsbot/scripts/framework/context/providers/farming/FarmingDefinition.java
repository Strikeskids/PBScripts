package org.logicail.rsbot.scripts.framework.context.providers.farming;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 11/04/2014
 * Time: 23:47
 */
public class FarmingDefinition {
	public static final FarmingDefinition NIL = new FarmingDefinition(-1, "", new int[0]);

	private final int id;
	private final String name;
	private final int[] models;

	public FarmingDefinition(int id, String name, int[] models) {
		this.id = id;
		this.name = name;
		this.models = models;
	}

	FarmingDefinition(JsonObject.Member member) {
		this.id = Integer.parseInt(member.getName());

		final JsonObject value = member.getValue().asObject();

		this.name = value.get("name").asString();

		final JsonArray modelArray = value.get("models").asArray();
		models = new int[modelArray.size()];
		int i = 0;
		for (JsonValue v : modelArray) {
			models[i] = v.asInt();
			i++;
		}
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public int[] models() {
		return models;
	}

	public boolean containsModel(int id) {
		for (int i : models) {
			if (i == id) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "FarmingDefinition{" +
				"id=" + id +
				", name='" + name + '\'' +
				", models=" + Arrays.toString(models) +
				'}';
	}
}
