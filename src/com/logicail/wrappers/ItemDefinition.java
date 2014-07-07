package com.logicail.wrappers;

import com.logicail.wrappers.loaders.ItemDefinitionLoader;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 10:37
 */
public class ItemDefinition extends Definition {
	public static ItemDefinitionLoader loader = null;
	public String name;
	public Boolean members;
	public String[] groundActions = new String[]{null, null, "Take", null, null};
	public Integer noteId;
	public String[] actions = new String[]{null, null, null, null, "Drop"};
	private short[] recolorOriginal;
	private int equippedModelMaleTranslationY;
	private int equippedModelMale2;
	private int equippedModelMale3;
	private int translateX = 0;
	private short[] r;
	private int noteTemplateId = -1;
	private int lightMag;
	private int equippedModelFemale;
	private int equippedModelFemaleDialogue1;
	private int modelId;
	private int[] stackSizes;
	private int rotationX = 0;
	private int team = 0;
	private int rotationZ = 0;
	private int stackOffset = 0;
	private int modelScaleZ = 128;
	private int equippedModelMale1;
	private int value = 1;
	private int equippedModelFemateTranslationY;
	private int lightIntensity;
	private int equippedModelMaleDiaglogue2;
	private int[] stackVarient;
	private int modelscaleY = 128;
	private int equippedModelFemateDialogue2;
	private int equippedModelFemale2;
	private int rotationY = 0;
	private short[] M;
	private int rotationLength = 2000;
	private int tranlateY = 0;
	private short[] recolorTarget;
	private int modelScaleX = 128;
	private int equippedModelFemale1;
	private int equippedModelMaleDialogue1;

	public ItemDefinition(int id, Stream stream) {
		super(id);

		decode(stream);

		if (noteTemplateId > -1) {
			fix(loader.get(noteTemplateId), loader.get(noteId));
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
		this.r = lhs.r;
		this.M = lhs.M;
		this.name = rhs.name;
		this.members = rhs.members;
		this.value = rhs.value;
		this.stackOffset = 1;
	}

	@Override
	protected void decode(Stream s, int opcode) {
		if (opcode == 1) {
			this.modelId = s.getUShort();
		} else if (opcode == 2) {
			this.name = s.getString();
		} else if (opcode == 4) {
			this.rotationLength = s.getUShort();
		} else if (opcode == 5) {
			this.rotationX = s.getUShort();
		} else if (opcode == 6) {
			this.rotationY = s.getUShort();
		} else if (opcode == 7) {
			this.translateX = s.getUShort();
			if (this.translateX > 32767) {
				this.translateX = this.translateX - 65536;
			}
		} else if (opcode == 8) {
			this.tranlateY = s.getUShort();
			if (this.tranlateY > 32767) {
				this.tranlateY = this.tranlateY - 65536;
			}
		} else if (11 == opcode) {
			this.stackOffset = 1;
		} else if (12 == opcode) {
			this.value = s.getInt();
		} else if (opcode == 16) {
			this.members = true;
		} else if (23 == opcode) {
			this.equippedModelMale1 = s.getUShort();
			this.equippedModelMaleTranslationY = s.getUByte();
		} else if (24 == opcode) {
			this.equippedModelMale2 = s.getUShort();
		} else if (opcode == 25) {
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
			int i1 = s.getUByte();
			this.recolorOriginal = new short[i1];
			this.recolorTarget = new short[i1];
			int i2 = 0;
			int i3 = 0;
			while (i2 < i1) {
				short[] a1 = this.recolorOriginal;
				int i4 = (short) s.getUShort();
				a1[i3] = (short) i4;
				short[] a2 = this.recolorTarget;
				int i5 = (short) s.getUShort();
				i2 = i3 + 1;
				a2[i3] = (short) i5;
				i3 = i2;
			}
		} else if (opcode == 41) {
			int i6 = s.getUByte();
			this.r = new short[i6];
			this.M = new short[i6];
			int i7 = 0;
			int i8 = 0;
			while (i7 < i6) {
				short[] a3 = this.r;
				int i9 = (short) s.getUShort();
				a3[i8] = (short) i9;
				short[] a4 = this.M;
				int i10 = (short) s.getUShort();
				i7 = i8 + 1;
				a4[i8] = (short) i10;
				i8 = i7;
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