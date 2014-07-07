package com.logicail.wrappers;

import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:31
 */
public class ScriptDefinition extends Definition {
	public int configId = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;

	public ScriptDefinition(int id, Stream stream) {
		super(id);
		decode(stream);
	}

	@Override
	public String toString() {
		return "ScriptDef " + id + " => ctx.varpbits.varpbit(" + configId + ", " + lowerBitIndex + ", 0x" + Integer.toHexString(mask()) + ")";
	}

	public int mask() {
		int mask = 0;
		int upper = upperBitIndex - lowerBitIndex;
		for (int i = 0; i <= upper; ++i) {
			mask += Math.pow(2, i);
		}
		return mask;
	}

	@Override
	protected void decode(Stream stream, int opcode) {
		if (opcode == 1) {
			configId = stream.getUShort();
			lowerBitIndex = stream.getUByte();
			upperBitIndex = stream.getUByte();
		} else {
			throw new IllegalArgumentException("Unknown opcode " + opcode);
		}
	}
}
