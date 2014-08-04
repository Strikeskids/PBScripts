package com.sk.cache.wrappers.protocol.extractor;

import com.sk.datastream.Stream;

public class ArrayExtractor extends FieldExtractor {

	private final int extra;
	private final StreamExtractor counter;
	private final StreamExtractor[] repeats;
	private final String[] fields;

	public ArrayExtractor(StreamExtractor counter, int extra, StreamExtractor[] repeat, String[] fieldNames) {
		super(counter);
		if (repeat == null)
			repeat = new StreamExtractor[0];
		if (fieldNames == null)
			fieldNames = new String[repeat.length];
		this.counter = counter;
		this.repeats = repeat;
		this.fields = fieldNames;
		this.extra = extra;
	}

	@Override
	public void read(Object destination, int minLoc, int type, Stream s) {
		int count = ((Number) counter.get(s)).intValue() + extra;
		Object[][] ret = new Object[repeats.length][count];
		for (int i = 0; i < count; ++i) {
			for (int j = 0; j < repeats.length; ++j) {
				if (repeats[j] != null) {
					ret[j][i] = repeats[j].get(s);
				}
			}
		}
		for (int j = 0; j < repeats.length; ++j) {
			if (j < fields.length && fields[j] != null) {
				setValue(destination, minLoc, type, fields[j], ret[j]);
			}
		}
	}
}
