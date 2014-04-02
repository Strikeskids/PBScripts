package com.sk.methods;

import org.logicail.rsbot.scripts.framework.context.IClientContext;
import org.powerbot.script.Keyboard;

import java.util.*;

public class SkKeyboard extends Keyboard {
	private static final Set<String> valid = new HashSet<String>();
	private static final Map<String, String> keyNames = new HashMap<String, String>();

	static {
		for (char c = 'A'; c <= 'Z'; ++c)
			valid.add(Character.toString(c));
		for (char c = '0'; c <= '9'; ++c)
			valid.add(Character.toString(c));
		for (int i = 1; i <= 24; ++i)
			valid.add("F" + i);
		valid.addAll(Arrays.asList("LEFT", "RIGHT", "UP", "DOWN"));
		keyNames.put("-", "MINUS");
		keyNames.put("=", "EQUALS");
		keyNames.put("\n", "ENTER");
		keyNames.put(".", "PERIOD");
		keyNames.put(",", "COMMA");
		keyNames.put(";", "SEMICOLON");
		keyNames.put("'", "QUOTE");
		keyNames.put("[", "OPEN_BRACKET");
		keyNames.put("]", "CLOSE_BRACKET");
		keyNames.put("\\", "BACKSLASH");
		keyNames.put("`", "BACK_QUOTE");
		keyNames.put("/", "SLASH");
	}

	private final IClientContext ctx;

	public SkKeyboard(IClientContext ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public boolean press(String key) {
		return key(key, 1);
	}

	public boolean key(String key, int type) {
		if (key == null)
			return false;
		String act;
		switch (type) {
			case 0:
				if (!key(key, 1))
					return false;
				ctx.sleep(100);
				return key(key, 2);
			case 1:
				act = "down";
				break;
			case 2:
				act = "up";
				break;
			default:
				throw new IllegalArgumentException("Type of interaction must be 0 (type) 1 (press) or 2 (release)");
		}
		return send("{VK_" + convert(key) + " " + act + "}");
	}

	public String convert(String key) {
		key = key.toUpperCase();
		if (valid.contains(key))
			return key;
		if (keyNames.containsKey(key))
			return keyNames.get(key);
		throw new IllegalArgumentException();
	}

	public boolean release(String key) {
		return key(key, 2);
	}
}
