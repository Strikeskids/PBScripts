package com.sk.oldcombatapi.util.time;


import org.powerbot.script.Random;

public class Delay {
	public static void sleep(long updatePeriod) {
		try {
			Thread.sleep(updatePeriod);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	public static void sleep(int min, int max) {
		sleep(Random.nextInt(min, max));
	}
}
