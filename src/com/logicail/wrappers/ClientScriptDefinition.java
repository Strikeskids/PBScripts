package com.logicail.wrappers;

import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 09/07/2014
 * Time: 22:23
 */
public class ClientScriptDefinition extends Definition {
	public int count;
	public int[] ints;
	public char input;
	public char output;
	public int defaultInt;
	public String defaultString = "null";
	public int[] otherints;
	public String[] stringOperands;

	public ClientScriptDefinition(int id, Stream stream) {
		super(id);
		decode(stream);
	}

	@Override
	protected void decode(Stream byteStream, int opcode) {
		if (1 == opcode)
			input = (char) byteStream.getUByte();
		else if (opcode == 2)
			output = (char) byteStream.getUByte();
		else if (opcode == 3)
			defaultString = byteStream.getString();
		else if (opcode == 4)
			defaultInt = byteStream.getInt();
		else if (5 == opcode) {
			count = byteStream.getUShort();
			ints = new int[count];
			stringOperands = new String[count];
			for (int i = 0; i < count; i++) {
				ints[i] = byteStream.getInt();
				stringOperands[i] = byteStream.getString();
			}
		} else if (6 == opcode) {
			count = byteStream.getUShort();
			ints = new int[count];
			otherints = new int[count];
			for (int i = 0; i < count; i++) {
				ints[i] = byteStream.getInt();
				otherints[i] = byteStream.getInt();
			}
		}
	}
}
