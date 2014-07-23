package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.WrapperLoader;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 10:37
 */
public class ItemDefinition extends Definition {
	public static WrapperLoader<ItemDefinition> loader = null;
	public String name = null;
	public boolean noted;
	public boolean members;
	public String[] groundActions = new String[]{null, null, "Take", null, null};
	public int noteId;
	public String[] actions = new String[]{null, null, null, null, "Drop"};
	protected short[] recolorOriginal;
	protected int equippedModelMaleTranslationY;
	protected int equippedModelMale2;
	protected int equippedModelMale3;
	protected int translateX = 0;
	protected short[] unknown41a;
	protected int noteTemplateId = -1;
	protected int lightMag;
	protected int equippedModelFemale;
	protected int equippedModelFemaleDialogue1;
	protected int modelId;
	protected int[] stackSizes;
	protected int rotationX = 0;
	protected int team = 0;
	protected int rotationZ = 0;
	protected int stackOffset = 0;
	protected int modelScaleZ = 128;
	protected int equippedModelMale1;
	protected int value = 1;
	protected int equippedModelFemateTranslationY;
	protected int lightIntensity;
	protected int equippedModelMaleDiaglogue2;
	protected int[] stackVarient;
	protected int modelscaleY = 128;
	protected int equippedModelFemateDialogue2;
	protected int equippedModelFemale2;
	protected int rotationY = 0;
	protected short[] unknown41b;
	protected int rotationLength = 2000;
	protected int tranlateY = 0;
	protected short[] recolorTarget;
	protected int modelScaleX = 128;
	protected int equippedModelFemale1;
	protected int equippedModelMaleDialogue1;

	public ItemDefinition(WrapperLoader<?> loader, int id) {
		super(loader, id);
	}


	public void fix() {
		if (noteTemplateId > -1) {
			noted = true;
			fix(loader.load(noteTemplateId), loader.load(noteId));
		}
	}

	private void fix(ItemDefinition lhs, ItemDefinition rhs) {
		this.modelId = lhs.modelId;
		this.rotationLength = lhs.rotationLength;
		this.rotationX = lhs.rotationX;
		this.rotationY = lhs.rotationY;
		this.rotationZ = lhs.rotationZ;
		this.translateX = lhs.translateX;
		this.tranlateY = lhs.tranlateY;
		this.recolorOriginal = lhs.recolorOriginal;
		this.recolorTarget = lhs.recolorTarget;
		this.unknown41a = lhs.unknown41a;
		this.unknown41b = lhs.unknown41b;
		this.name = rhs.name;
		this.members = rhs.members;
		this.value = rhs.value;
		this.stackOffset = 1;
	}

	@Override
	protected void decode(Stream s, int opcode) {
		if (1 == opcode) {
			this.modelId = s.getUShort();
		} else if (2 == opcode) {
			this.name = s.getString();
		} else if (4 == opcode) {
			this.rotationLength = s.getUShort();
		} else if (5 == opcode) {
			this.rotationX = s.getUShort();
		} else if (6 == opcode) {
			this.rotationY = s.getUShort();
		} else if (7 == opcode) {
			this.translateX = s.getUShort();
			if (this.translateX > 0x7fff) {
				this.translateX = this.translateX - 0x10000;
			}
		} else if (8 == opcode) {
			this.tranlateY = s.getUShort();
			if (this.tranlateY > 0x7fff) {
				this.tranlateY = this.tranlateY - 0x10000;
			}
		} else if (11 == opcode) {
			this.stackOffset = 1;
		} else if (12 == opcode) {
			this.value = s.getInt();
		} else if (16 == opcode) {
			this.members = true;
		} else if (23 == opcode) {
			this.equippedModelMale1 = s.getUShort();
			this.equippedModelMaleTranslationY = s.getUByte();
		} else if (24 == opcode) {
			this.equippedModelMale2 = s.getUShort();
		} else if (25 == opcode) {
			this.equippedModelFemale1 = s.getUShort();
			this.equippedModelFemateTranslationY = s.getUByte();
		} else if (opcode == 26) {
			this.equippedModelFemale2 = s.getUShort();
		} else if (opcode >= 30 && opcode < 35) {
			this.groundActions[opcode - 30] = s.getString();
			if (this.groundActions[opcode - 30].equalsIgnoreCase("Hidden")) {
				this.groundActions[opcode - 30] = null;
			}
		} else if (opcode >= 35 && opcode < 40) {
			this.actions[opcode - 35] = s.getString();
		} else if (opcode == 40) {
			int length = s.getUByte();
			this.recolorOriginal = new short[length];
			this.recolorTarget = new short[length];
			for (int i = 0; i < length; i++) {
				recolorOriginal[i] = (short) s.getUShort();
				recolorTarget[i] = (short) s.getUShort();
			}
		} else if (opcode == 41) {
			int length = s.getUByte();
			this.unknown41a = new short[length];
			this.unknown41b = new short[length];
			for (int i = 0; i < length; i++) {
				unknown41a[i] = (short) s.getUShort();
				unknown41b[i] = (short) s.getUShort();
			}
		} else if (opcode == 78) {
			this.equippedModelMale3 = s.getUShort();
		} else if (79 == opcode) {
			this.equippedModelFemale = s.getUShort();
		} else if (opcode == 90) {
			this.equippedModelMaleDialogue1 = s.getUShort();
		} else if (91 == opcode) {
			this.equippedModelFemaleDialogue1 = s.getUShort();
		} else if (opcode == 92) {
			this.equippedModelMaleDiaglogue2 = s.getUShort();
		} else if (93 == opcode) {
			this.equippedModelFemateDialogue2 = s.getUShort();
		} else if (95 == opcode) {
			this.rotationZ = s.getUShort();
		} else if (opcode == 97) {
			this.noteId = s.getUShort();
		} else if (98 == opcode) {
			this.noteTemplateId = s.getUShort();
		} else if (opcode >= 100 && opcode < 110) {
			if (this.stackVarient == null) {
				this.stackVarient = new int[10];
				this.stackSizes = new int[10];
			}
			this.stackVarient[opcode - 100] = s.getUShort();
			this.stackSizes[opcode - 100] = s.getUShort();
		} else if (opcode == 110) {
			this.modelScaleX = s.getUShort();
		} else if (111 == opcode) {
			this.modelscaleY = s.getUShort();
		} else if (112 == opcode) {
			this.modelScaleZ = s.getUShort();
		} else if (113 == opcode) {
			this.lightIntensity = (int) s.getByte();
		} else if (opcode == 114) {
			this.lightMag = (int) s.getByte();
		} else if (opcode == 115) {
			this.team = s.getUByte();
		}
	}
}