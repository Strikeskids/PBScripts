package org.logicail.rsbot.scripts.loggildedaltar.tasks;

import org.powerbot.script.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/06/12
 * Time: 14:46
 */
public class OpenHouse implements Comparable<OpenHouse> {
	private long added;
	private long success;
	private final String playerName;
	private long skipUntil;
	private boolean hasObelisk = true;

	public OpenHouse(String playerName) {
		this.added = System.currentTimeMillis();
		this.success = System.currentTimeMillis();
		this.playerName = playerName.toLowerCase();
	}

	public void setHasObelisk(boolean value) {
		hasObelisk = value;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setEntered() {
		success = System.currentTimeMillis();
	}

	public void setSkipping() {
		skipUntil = System.currentTimeMillis() + Random.nextInt(5 * 60 * 1000, 9 * 60 * 1000);
	}

	public long getSuccess() {
		return success;
	}

	public long getTimeAdded() {
		return added;
	}

	@Override
	public int hashCode() {
		return getPlayerName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && (obj == this || obj.getClass() == getClass() && hashCode() == obj.hashCode());
	}

	public void setTimeAdded(long timeAdded) {
		added = timeAdded;
	}

	public boolean isSkipping() {
		return System.currentTimeMillis() < skipUntil;
	}

	@Override
	public int compareTo(OpenHouse o) {
		double dist = getSuccess() - o.getSuccess();

		final SummoningTask summoningTask = SummoningTask.getInstance();
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

	@Override
	public String toString() {
		return "House: " + playerName + " isSkipping: " + isSkipping();
	}
}
