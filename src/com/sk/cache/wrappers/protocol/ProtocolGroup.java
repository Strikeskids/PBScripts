package com.sk.cache.wrappers.protocol;

import com.sk.datastream.Stream;

import java.util.HashMap;
import java.util.Map;

public class ProtocolGroup extends ProtocolReader {

	private Map<Integer, ProtocolReader> readers = new HashMap<Integer, ProtocolReader>();

	public void addReader(ProtocolReader reader, int... locs) {
		for (int loc : locs) {
			readers.put(loc, reader);
		}
	}

	public ProtocolReader getReader(int type) {
		return readers.get(type);
	}

	@Override
	public void read(Object destination, int type, Stream data) {
		final byte[] bytes = data.getAllBytes();
		if (!validateType(type))
			throw new IllegalArgumentException("Bad type " + type);
		ProtocolReader reader = readers.get(type);
		reader.read(destination, type, data);
	}

	@Override
	public boolean validateType(int type) {
		return readers.containsKey(type);
	}

	@Override
	public void addSelfToGroup(ProtocolGroup group) {
		throw new UnsupportedOperationException();
	}
}
