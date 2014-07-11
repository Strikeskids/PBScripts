package com.sk.oldcombatapi.util.time;

import com.sk.oldcombatapi.util.Condition;
import org.logicail.rsbot.scripts.framework.util.Timer;

public abstract class TimeAfterCondition extends Timer implements Condition, Waitable {
	public TimeAfterCondition(long repeat) {
		super(repeat);
	}

	@Override
	public boolean waitFor(long maxTime) {
		reset();
		boolean ret = false;
		for (Timer end = new Timer(maxTime); end.running() && !(ret = this.running()); Delay.sleep(30, 50))
			;
		return ret;
	}

	@Override
	public boolean running() {
		if (check())
			reset();
		return super.running();
	}

	@Override
	public boolean waitFor(long maxTime, long delay) {
		reset();
		boolean ret = false;
		for (Timer end = new Timer(maxTime); end.running() && !(ret = this.running()); Delay.sleep(delay))
			;
		return ret;
	}
}
