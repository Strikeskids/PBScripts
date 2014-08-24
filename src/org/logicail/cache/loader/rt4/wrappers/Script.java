package org.logicail.cache.loader.rt4.wrappers;

import org.logicail.cache.loader.rt4.wrappers.loaders.ScriptDefinitionLoader;
import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.datastream.Stream;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:31
 */
public class Script extends StreamedWrapper {
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
		super(loader, id);
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

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				configId = s.getUShort();
				lowerBitIndex = s.getUByte();
				upperBitIndex = s.getUByte();
			}
		}
	}
}
