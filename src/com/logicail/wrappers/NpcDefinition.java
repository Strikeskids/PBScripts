package com.logicail.wrappers;

import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/07/2014
 * Time: 18:00
 */
public class NpcDefinition extends Definition {
	public String name;
	public Integer combatLevel;
	public String[] actions = new String[5];
	public Integer idleAnimation;
	public int[] models;
	public int[] headModels;
	public Integer scriptId;
	public Integer configId;
	public int[] childrenIds;
	private short[] recolorTarget;
	private boolean visible = false;
	private int walkAnimation = -1;
	private int scaleY = 128;
	private int turn90CCWAnimation = -1;
	private int turn180Animation = -1;
	private boolean clickable = true;
	private int degreesToTurn = 32;
	private short[] recolorOriginal;
	private boolean drawMiniMapDot = true;
	private short[] unknown41a;
	private int lightModifier = 0;
	private int scaleXZ = 128;
	private int turn90CWAnimation = -1;
	private int boundDim = -1;
	private short[] unknown41b;
	private int headIcon = -1;
	private int shadowModifier = 0;

	public NpcDefinition(int id, Stream stream) {
		super(id);
		decode(stream);
	}

	@Override
	protected void decode(Stream stream, int opcode) {
		if (1 == opcode) {
			int count = stream.getUByte();
			this.models = new int[count];
			for (int k = 0; k < count; k++) {
				models[k] = stream.getUShort();
			}
		} else if (opcode == 2) {
			this.name = stream.getString();
		} else if (opcode == 12) {
			this.boundDim = stream.getUByte();
		} else if (13 == opcode) {
			this.idleAnimation = stream.getUShort();
		} else if (opcode == 14) {
			this.walkAnimation = stream.getUShort();
		} else if (15 == opcode) {
			stream.getUShort();
		} else if (16 == opcode) {
			stream.getUShort();
		} else if (opcode == 17) {
			this.walkAnimation = stream.getUShort();
			this.turn180Animation = stream.getUShort();
			this.turn90CWAnimation = stream.getUShort();
			this.turn90CCWAnimation = stream.getUShort();
		} else if (opcode >= 30 && opcode < 35) {
			this.actions[opcode - 30] = stream.getString();
			if (this.actions[opcode - 30].equalsIgnoreCase("Hidden")) {
				this.actions[opcode - 30] = null;
			}
		} else if (40 == opcode) {
			int length = stream.getUByte();
			this.recolorOriginal = new short[length];
			this.recolorTarget = new short[length];
			for (int i = 0; i < length; i++) {
				recolorOriginal[i] = (short) stream.getUShort();
				recolorTarget[i] = (short) stream.getUShort();
			}
		} else if (opcode == 41) {
			int length = stream.getUByte();
			this.unknown41a = new short[length];
			this.unknown41b = new short[length];
			for (int i = 0; i < length; i++) {
				unknown41a[i] = (short) stream.getUShort();
				unknown41b[i] = (short) stream.getUShort();
			}
		} else if (60 == opcode) {
			int count = stream.getUByte();
			this.headModels = new int[count];
			for (int i = 0; i < count; i++) {
				headModels[i] = stream.getUShort();
			}
		} else if (93 == opcode) {
			this.drawMiniMapDot = false;
		} else if (95 == opcode) {
			this.combatLevel = stream.getUShort();
		} else if (97 == opcode) {
			this.scaleXZ = stream.getUShort();
		} else if (opcode == 98) {
			this.scaleY = stream.getUShort();
		} else if (opcode == 99) {
			this.visible = true;
		} else if (opcode == 100) {
			this.lightModifier = (int) stream.getByte();
		} else if (opcode == 101) {
			this.shadowModifier = (int) stream.getByte();
		} else if (102 == opcode) {
			this.headIcon = stream.getUShort();
		} else if (103 == opcode) {
			this.degreesToTurn = stream.getUShort();
		} else if (106 == opcode) {
			this.scriptId = stream.getUShort();
			if (this.scriptId == 65535) {
				this.scriptId = -1;
			}
			this.configId = stream.getUShort();
			if (this.configId == 65535) {
				this.configId = -1;
			}
			int count = stream.getUByte() + 1; // check
			this.childrenIds = new int[1 + count];
			for (int i = 0; i < count; ++i) {
				this.childrenIds[i] = stream.getUShort();
				if (this.childrenIds[i] == 65535) {
					this.childrenIds[i] = -1;
				}
			}
			childrenIds[count] = -1;
		} else if (opcode == 107) {
			this.clickable = false;
		}
	}
}
