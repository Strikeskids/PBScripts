package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.ScriptDefinitionLoader;
import com.sk.cache.wrappers.ProtocolWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.FieldExtractor;
import com.sk.cache.wrappers.protocol.extractor.ParseType;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:31
 */
public class Script extends ProtocolWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();
	public static final int[] MASKS;

	static {
		MASKS = new int[32];
		int x = 2;
		for (int i = 0; i < 32; i++) {
			MASKS[i] = x - 1;
			x += x;
		}
	}

	public int configId = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;

	public Script(ScriptDefinitionLoader loader, int id) {
		super(loader, id, protocol);
	}

	static {
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "configId"), new FieldExtractor(ParseType.UBYTE, "lowerBitIndex"), new FieldExtractor(ParseType.UBYTE, "upperBitIndex")}, 1).addSelfToGroup(protocol);

	}

	@Override
	public String toString() {
		return "Script " + id + " => " + code();
	}

	public String code() {
		return "ctx.varpbits.varpbit(" + configId + ", " + lowerBitIndex + ", 0x" + Integer.toHexString(MASKS[upperBitIndex - lowerBitIndex]) + ")";
	}

	public int execute(ClientContext ctx) {
		return ctx.varpbits.varpbit(configId) >> lowerBitIndex & MASKS[upperBitIndex - lowerBitIndex];
	}
}
