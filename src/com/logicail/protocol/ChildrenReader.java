package com.logicail.protocol;

import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.ProtocolReader;
import com.sk.cache.wrappers.protocol.extractor.FieldExtractor;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 24/07/2014
 * Time: 21:13
 */
public class ChildrenReader extends ProtocolReader {

	private final int loc;

	public ChildrenReader(int loc) {
		this.loc = loc;
	}

	@Override
	public void read(Object destination, int type, Stream s) {
		int script = s.getUShort();
		int config = s.getUShort();
		int count = s.getUByte();
		int[] children = new int[count + 1];
		for (int i = 0; i < children.length; i++) {
			children[i] = s.getUShort();
			if (children[i] == 0xffff) {
				children[i] = -1;
			}
		}
		FieldExtractor.setValue(destination, type, type, "scriptId", script == 0xFFFF ? -1 : script);
		FieldExtractor.setValue(destination, type, type, "configId", config == 0xFFFF ? -1 : config);
		FieldExtractor.setValue(destination, type, type, "childrenIds", children);
	}

	@Override
	public boolean validateType(int type) {
		return type == loc;
	}

	@Override
	public void addSelfToGroup(ProtocolGroup group) {
		group.addReader(this, loc);
	}

}
