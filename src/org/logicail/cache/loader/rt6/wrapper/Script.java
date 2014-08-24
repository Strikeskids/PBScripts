package org.logicail.cache.loader.rt6.wrapper;

import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.datastream.Stream;
import org.logicail.cache.loader.rt6.wrapper.loaders.ScriptLoader;
import org.powerbot.script.rt4.ClientContext;

public class Script extends StreamedWrapper {
	public static final int[] MASKS;

	public int configId = -1;
	public int configType = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;

	static {
		MASKS = new int[32];
		int x = 2;
		for (int i = 0; i < 32; i++) {
			MASKS[i] = x - 1;
			x += x;
		}
	}

	public Script(ScriptLoader loader, int id) {
		super(loader, id);
	}

	@Override
	public String toString() {
		return "Script " + id + " => " + code();
	}

	public String code() {
		return "ctx.varpbits.varpbit(" + configId + ", " + lowerBitIndex + ", 0x" + Integer.toHexString(mask()) + ")";
	}

	public int mask() {
		return MASKS[upperBitIndex - lowerBitIndex];
	}

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				configType = s.getUByte();
				configId = s.getBigSmart();
			} else if (opcode == 2) {
				lowerBitIndex = s.getUByte();
				upperBitIndex = s.getUByte();
			}
		}
	}

	public int execute(ClientContext ctx) {
		return ctx.varpbits.varpbit(configId) >> lowerBitIndex & mask();
	}
}
