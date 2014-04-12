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
	public final int id;
	public final String name;
	public final String[] actions;
	public final int[] models;

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

		final JsonArray actionsArray = value.get("actions").asArray();
		actions = new String[actionsArray.size()];
		i = 0;
		for (JsonValue v : actionsArray) {
			actions[i] = v.asString();
			i++;
		}
	}

	@Override
	public String toString() {
		return "FarmingDefinition{" +
				"id=" + id +
				", name='" + name + '\'' +
				", actions=" + Arrays.toString(actions) +
				", models=" + Arrays.toString(models) +
				'}';
	}
}
