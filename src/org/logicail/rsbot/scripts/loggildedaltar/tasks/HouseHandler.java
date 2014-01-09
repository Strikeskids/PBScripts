package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.framework.context.IMethodProvider;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.script.util.Hiscores;
import org.powerbot.script.util.Random;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/12/13
 * Time: 21:41
 */
public class HouseHandler extends IMethodProvider implements MessageListener {
	private static final Pattern[] ADVERT_PATTERNS = {
			//Pattern.compile(".*>([\\d\\w\\s-]+)<.*altar.*"), //>name<  altar
			//Pattern.compile(".*altar.*>([\\d\\w\\s-]+)<.*"), //altar >name<
			Pattern.compile(".*\"([a-zA-Z\\d\\s-]+)\".*"), //"name"
			Pattern.compile(".*\\*([a-zA-Z\\d\\s-]+)\\*.*"), //*name*
			Pattern.compile(">([a-zA-Z\\d\\s-]+)<"), //>name<
			Pattern.compile("\\[([a-zA-Z\\d\\s-]+)]"), //[name]
			Pattern.compile("#([a-zA-Z\\d\\s-]+)#"), //#name#
			Pattern.compile(".* ([a-zA-Z\\d\\s-]+)\\s*/"), // name/...
			//Pattern.compile(".*\\[([a-zA-Z\d\s-]+)].*altar.*"), //[name] altar
			//Pattern.compile(".*altar.*\\[([a-zA-Z\d\s-]+)].*"), //altar [name]
			Pattern.compile(".*altar.*at ([a-zA-Z\\d\\s-]+)"), //altar at name
			Pattern.compile(".*join ([a-zA-Z\\d\\s-]+) for.*altar.*"), //join name for
			Pattern.compile("([a-zA-Z\\d\\s-]+) -.*altar.*"), //name - *altar
			Pattern.compile("([a-zA-Z\\d\\s-]+) for.*altar.*"), //name for altar
			Pattern.compile(".*altar.*@([a-zA-Z\\d\\s-]+)") //altar@name
	};
	private static final String URL_GET_HOUSES = "http://logicail.co.uk/get_houses.php";
	public final PriorityQueue<OpenHouse> openHouses = new PriorityQueue<OpenHouse>();
	public final Set<String> ignoreHouses = new HashSet<String>();
	private final AtomicReference<OpenHouse> current_house = new AtomicReference<OpenHouse>();
	private final LogGildedAltar script;
	private final HashMap<String, Boolean> checkedNames = new HashMap<String, Boolean>();
	private int nextTimeBounds = 900000;
	private long waitUntil = System.currentTimeMillis();
	private long nextCheckForhouses = 0;

	public HouseHandler(LogGildedAltar script) {
		super(script.ctx);
		this.script = script;
		ignoreHouses.add("lit g altar");
		ignoreHouses.add("altar");
		ignoreHouses.add("fc");
		ignoreHouses.add("400k");
		ignoreHouses.add("advertiser");
		ignoreHouses.add("lighter");
		ignoreHouses.add("us");
		ignoreHouses.add("galtar");
	}

	public void currentHouseFailed() {
		OpenHouse current = current_house.get();
		if (current != null) {
			current.setSkipping();
			script.log.info("Ignoring house at \"" + current.getPlayerName() + "\" for a while");
			current_house.set(null);
		}
	}

	public OpenHouse getCurrentHouse() {
		return current_house.get();
	}

	public OpenHouse getOpenHouse() {
		OpenHouse current = current_house.get();
		if (current != null) {
			return current;
		}

		final Iterator<OpenHouse> iterator = openHouses.iterator();
		final long timeBounds = System.currentTimeMillis() - nextTimeBounds;

		while (iterator.hasNext()) {
			final OpenHouse house = iterator.next();

			if (house.getTimeAdded() < timeBounds && house.getSuccess() < timeBounds) {
				iterator.remove();
				nextTimeBounds = Random.nextInt(900000, 1200000);
			} else {
				if (!house.isSkipping()) {
					current_house.set(house);
					return house;
				}
			}
		}

		if (script.options.detectHouses && System.currentTimeMillis() > nextCheckForhouses) {
			nextCheckForhouses = System.currentTimeMillis() + 600000;
			addOpenHouses();
		}

		return null;
	}

	/**
	 * Request open houses from server
	 */
	public void addOpenHouses() {
		nextCheckForhouses = System.currentTimeMillis() + 600000; // 10 minutes
		final String json = script.downloadString(URL_GET_HOUSES);

		try {
			JsonObject jsonObject = JsonObject.readFrom(json);
			final JsonValue open_houses = jsonObject.get("open_houses");
			if (open_houses != null && open_houses.isArray()) {
				final JsonArray houses = open_houses.asArray();
				for (JsonValue house : houses) {
					if (house.isString()) {
						final String playername = house.asString();
						if (!ignoreHouses.contains(playername)) {
							final OpenHouse openHouse = new OpenHouse(script, playername);
							if (!openHouses.contains(openHouse)) {
								openHouses.add(openHouse);
								script.log.info("Added house at \"" + playername + "\"");
							}
						}
						checkedNames.put(playername, true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		if (messageEvent.getId() == 0) { // TODO find friends chat
			parseHouses(messageEvent.getMessage());
		}
	}

	/**
	 * Parse house names from chat
	 *
	 * @param message
	 */
	public void parseHouses(String message) {
		message = message.toLowerCase().trim();
		if (/*(!message.contains("altar") && !message.contains("alter") && !message.contains("house"))*/
				message.length() == 0
						|| message.contains("world")
						|| message.contains(" bot ")
						|| message.contains("friends chat")) {
			return;
		}
		for (Pattern p : ADVERT_PATTERNS) {
			Matcher matcher = p.matcher(message);
			if (matcher.find()) {
				String player = matcher.group(1).trim();
				if (player.length() > 12) {
					// Invalid name
					continue;
				}
				if (!player.isEmpty() && !player.contains("join") && !player.contains(".")
						&& !ignoreHouses.contains(player)) {
					final OpenHouse house = new OpenHouse(script, player);
					if (!openHouses.contains(house)) {
						if (validatePlayer(player)) {
							openHouses.add(house);
							script.log.info("Added house at \"" + player + "\"");
						}
					}
					break;
				}
			}
		}
	}

	boolean validatePlayer(String name) {
		String underscoreName = name.replaceAll(" ", "_");

		if (checkedNames.containsKey(name)) {
			return checkedNames.get(name);
		}

		if (System.currentTimeMillis() < waitUntil) {
			return false;
		}

		waitUntil = System.currentTimeMillis() + 10000;

		try {
			Hiscores lookup = Hiscores.getProfile(underscoreName);
			return checkedNames.put(name, lookup != null);
		} catch (Exception ignored) {
		}

		return checkedNames.put(name, false);
	}
}
