package com.sk.cache.wrappers;

import com.sk.cache.wrappers.loaders.WrapperLoader;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.datastream.Stream;

import java.util.Map;

public class ProtocolWrapper extends StreamedWrapper {

	private final ProtocolGroup protocol;
	public Map<Integer, Object> attributes = null;

	public ProtocolWrapper(WrapperLoader<?> loader, int id, ProtocolGroup protocol) {
		super(loader, id);
		this.protocol = protocol;
	}

	@Override
	public void decode(Stream stream) {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			protocol.read(this, opcode, stream);
		}
	}
}
