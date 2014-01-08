package com.sk.util;

public final class Switch implements Condition {
	private boolean value;

	public Switch() {
		this(false);
	}

	public Switch(boolean init) {
		this.value = init;
	}

	@Override
	public boolean check() {
		return value;
	}

	public void flip() {
		set(!value);
	}

	public void off() {
		set(false);
	}

	public void set(boolean v) {
		this.value = v;
	}

	public void on() {
		set(true);
	}
}
