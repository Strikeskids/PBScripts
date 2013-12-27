package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.framework.LogicailScript;
import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltarSettings;
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
public class HouseHandler implements MessageListener {
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
	public final PriorityQueue<OpenHouse> openHouses = new PriorityQueue<OpenHouse>();
	public final Set<String> ignoreHouses = new HashSet<String>();
	private final AtomicReference<OpenHouse> current_house = new AtomicReference<OpenHouse>();
	private final LogicailScript script;
	private final HashMap<String, Boolean> checkedNames = new HashMap<String, Boolean>();
	public boolean houseValid;
	private int nextTimeBounds = 900000;
	private long waitUntil = System.currentTimeMillis();
	private long nextCheckForhouses = 0;

	public HouseHandler(LogicailScript script) {
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
					houseValid = false;
					return house;
				}
			}
		}

		if (LogGildedAltarSettings.detectHouses && System.currentTimeMillis() > nextCheckForhouses) {
			nextCheckForhouses = System.currentTimeMillis() + 600000;
			addOpenHouses();
		}

		return null;
	}

	public void currentHouseFailed() {
		OpenHouse current = current_house.get();
		if (current != null) {
			current.setSkipping();
			//GildedAltar.get().getLogHandler().print("Ignoring house at \"" + current.getPlayerName() + "\" for a while", Color.RED);
			current_house.set(null);
		}
		houseValid = false;
	}

	public void noAltar() {
	    /*OpenHouse current = current_house.get();
		if (current != null) {
			openHouses.remove(current);
			ignoreHouses.add(current.getPlayerName());
			current_house.set(null);
		}
		houseValid = false;*/
		currentHouseFailed();
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
					final OpenHouse house = new OpenHouse(player);
					if (!openHouses.contains(house)) {
						if (validatePlayer(player)) {
							openHouses.add(house);
							//GildedAltar.get().getLogHandler().print("Added house at \"" + player + "\"");
						}
					}
					break;
				}
			}
		}
	}

	boolean validatePlayer(String name) {
		String underscoreName = name.replaceAll(" ", "_");

		if (checkedNames.containsKey(underscoreName)) {
			return checkedNames.get(underscoreName);
		}

		if (System.currentTimeMillis() < waitUntil) {
			return false;
		}

		waitUntil = System.currentTimeMillis() + 10000;

		try {
			Hiscores lookup = Hiscores.getProfile(underscoreName);
			return checkedNames.put(underscoreName, lookup != null);
		} catch (Exception ignored) {
		}

		return checkedNames.put(underscoreName, false);
	}

	/**
	 * Request open houses from server
	 */
	public void addOpenHouses() {
		nextCheckForhouses = System.currentTimeMillis() + 600000; // 10 minutes
		/*HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) (new URL("http://logicail.co.uk/get_houses.php")).openConnection();
			connection.addRequestProperty("User-Agent", script.useragent);
			connection.addRequestProperty("Connection", "close");
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);

			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = new ContainerFactory() {
				public List createArrayContainer() {
					return new LinkedList();
				}

				public Map createObjectContainer() {
					return new LinkedHashMap();
				}
			};

			Map parsed = (Map) parser.parse(new InputStreamReader(connection.getInputStream()), containerFactory);

			if (parsed.containsKey("open_houses") && parsed.get("open_houses") instanceof LinkedList) {
				LinkedList<String> houses = (LinkedList) parsed.get("open_houses");
				for (String playername : houses) {
					if (!ignoreHouses.contains(playername)) {
						final OpenHouse house = new OpenHouse(playername);
						if (!openHouses.contains(house)) {
							openHouses.add(house);
							GildedAltar.get().getLogHandler().print("Added house at \"" + playername + "\"");
						}
					}
					checkedNames.put(playername, true);
				}
			}
		} catch (Exception ignored) {
			ignored.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}*/
	}

	@Override
	public void messaged(MessageEvent messageEvent) {
		if (messageEvent.getId() == 0) { // TODO
			parseHouses(messageEvent.getMessage());
		}
	}
}
