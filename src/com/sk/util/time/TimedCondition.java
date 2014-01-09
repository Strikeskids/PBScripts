package com.sk.util.time;

import com.sk.util.Condition;
import org.powerbot.script.util.Timer;

public abstract class TimedCondition implements Condition, Waitable {
	public TimedCondition() {
	}

	@Override
	public boolean waitFor(long maxTime) {
		for (Timer end = new Timer(maxTime); end.isRunning(); ) {
			if (this.check())
				return true;
			Delay.sleep(50, 100);
		}
		return false;
	}

	@Override
	public boolean waitFor(long maxTime, long delay) {
		for (Timer end = new Timer(maxTime); end.isRunning(); ) {
			if (this.check())
				return true;
			Delay.sleep(delay);
		}
		return false;
	}
}
