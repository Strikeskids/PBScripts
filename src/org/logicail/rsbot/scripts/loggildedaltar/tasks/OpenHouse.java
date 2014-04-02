package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.logicail.rsbot.scripts.loggildedaltar.LogGildedAltar;
import org.powerbot.script.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/06/12
 * Time: 14:46
 */
public class OpenHouse implements Comparable<OpenHouse> {
	private final LogGildedAltar script;
	private long added;
	private long success;
	private final String playerName;
	private long skipUntil;
	private boolean hasObelisk = true;

	public OpenHouse(LogGildedAltar script, String playerName) {
		this.script = script;
		this.added = System.currentTimeMillis();
		this.success = System.currentTimeMillis();
		this.playerName = playerName.toLowerCase();
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getPlayerNameClean() {
		return playerName.toLowerCase().replace("_", " ");
	}

	public long getSuccess() {
		return success;
	}

	public void setHasObelisk(boolean value) {
		hasObelisk = value;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && (obj == this || obj.getClass() == getClass() && hashCode() == obj.hashCode());
	}

	@Override
	public int hashCode() {
		return getPlayerName().hashCode();
	}

	@Override
	public String toString() {
		return "House: " + playerName + " isSkipping: " + isSkipping();
	}

	public boolean isSkipping() {
		return System.currentTimeMillis() < skipUntil;
	}

	@Override
	public int compareTo(OpenHouse o) {
		double dist = getSuccess() - o.getSuccess();

		final SummoningTask summoningTask = script.summoningTask;
		if (summoningTask != null && summoningTask.branch()) {
			// Need to recharge pick house with obelisk
			if (hasObelisk) {
				if (!o.hasObelisk) {
					return 1;
				}
			} else if (o.hasObelisk) {
				return -1;
			}
		}

		if (dist == 0) {
			return 0;
		} else {
			return dist < 0 ? -1 : 1;
		}
	}

	public long getTimeAdded() {
		return added;
	}

	public void setEntered() {
		success = System.currentTimeMillis();
	}

	public void setSkipping() {
		skipUntil = System.currentTimeMillis() + Random.nextInt(5 * 60 * 1000, 9 * 60 * 1000);
	}

	public void setTimeAdded(long timeAdded) {
		added = timeAdded;
	}
}
