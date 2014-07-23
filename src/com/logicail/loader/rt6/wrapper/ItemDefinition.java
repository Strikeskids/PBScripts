package com.logicail.loader.rt6.wrapper;

import com.logicail.loader.rt4.wrappers.Definition;
import com.logicail.loader.rt4.wrappers.loaders.WrapperLoader;
import com.logicail.loader.rt6.Parameter;
import com.sk.datastream.Stream;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:32
 */
public class ItemDefinition extends Definition {
	public static WrapperLoader<ItemDefinition> loader = null;
	public String name = null;
	public boolean noted;
	public boolean lent;
	public boolean cosmetic;
	public boolean members;
	public String[] groundActions = new String[]{null, null, "Take", null, null};
	public int noteId = -1;
	public String[] actions = new String[]{null, null, null, null, "Drop"};
	protected short[] recolorOriginal;
	protected int equippedModelMaleTranslationY;
	protected int equippedModelMale2;
	protected int equippedModelMale3;
	protected int translateX = 0;
	protected short[] unknown41a;
	public int noteTemplateId = -1;
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
	public int value = 1;
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
	public int slot;
	private HashMap<Integer, Object> clientScriptData;
	public HashMap<Parameter, Object> parameters;
	public LinkedHashMap<Integer, Object> unknowns;
	public boolean tradable;
	private int equipmentType;
	private int lentId;
	public int categoryId;
	private int cosmeticId;
	private int cosmeticTemplateId;
	private int lentTemplateId;

	public ItemDefinition(WrapperLoader<ItemDefinition> loader, int id) {
		super(loader, id);
	}

	@Override
	protected void decode(Stream s, int opcode) {
		//System.out.println(opcode);
		if (1 == opcode) {
			this.modelId = s.getBigSmart();
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
		} else if (13 == opcode) {
			this.slot = s.getUByte();
		} else if (14 == opcode) {
			equipmentType = s.getUByte();
		} else if (16 == opcode) {
			this.members = true;
		} else if (18 == opcode) {
			s.getUShort();
		} else if (23 == opcode) {
			this.equippedModelMale1 = s.getBigSmart();
		} else if (24 == opcode) {
			this.equippedModelFemale1 = s.getBigSmart();
		} else if (25 == opcode) {
			this.equippedModelMale2 = s.getBigSmart();
		} else if (opcode == 26) {
			this.equippedModelFemale2 = s.getBigSmart();
		} else if (opcode == 27) {
			s.getUByte();
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
				unknown41a[i] = s.getShort();
				unknown41b[i] = s.getShort();
			}
		} else if (opcode == 42) {
			int length = s.getUByte();
			for (int i = 0; i < length; i++) {
				s.getByte();
			}
		} else if (opcode == 43) {
			s.getInt();
		} else if (opcode == 44) {
			s.getUShort();
		} else if (opcode == 45) {
			s.getUShort();
		} else if (opcode == 64) {
			// set
		} else if (opcode == 65) {
			tradable = true;
		} else if (opcode == 78) {
			this.equippedModelMale3 = s.getBigSmart();
		} else if (79 == opcode) {
			this.equippedModelFemale = s.getBigSmart();
		} else if (opcode == 90) {
			this.equippedModelMaleDialogue1 = s.getBigSmart();
		} else if (91 == opcode) {
			this.equippedModelFemaleDialogue1 = s.getBigSmart();
		} else if (opcode == 92) {
			this.equippedModelMaleDiaglogue2 = s.getBigSmart();
		} else if (93 == opcode) {
			this.equippedModelFemateDialogue2 = s.getBigSmart();
		} else if (94 == opcode) {
			this.categoryId = s.getUShort();
		} else if (95 == opcode) {
			this.rotationZ = s.getUShort();
		} else if (opcode == 96) {
			s.getUByte();
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
		} else if (opcode == 121) {
			this.lentId = s.getUShort();
		} else if (opcode == 122) {
			this.lentTemplateId = s.getUShort();
		} else if (opcode == 125 || opcode == 126) {
			s.getByte();
			s.getByte();
			s.getByte();
		} else if (opcode == 132) {
			int length = s.getUByte();
			for (int i = 0; i < length; i++) {
				s.getUShort();
			}
		} else if (opcode == 134) {
			s.getUByte();
		} else if (opcode == 139) {
			this.cosmeticId = s.getUShort();
		} else if (opcode == 140) {
			this.cosmeticTemplateId = s.getUShort();
		} else if (opcode >= 142 && opcode <= 146) {
			s.getUShort();
		} else if (opcode >= 150 && opcode <= 154) {
			s.getUShort();
		} else if (opcode >= 161 && opcode <= 163) {
			s.getUShort();
		} else if (opcode == 164) {
			s.getString();
		} else if (opcode == 165) {
		} else if (opcode == 249) {
			int length = s.getUByte();

			clientScriptData = new LinkedHashMap<Integer, Object>();

			for (int index = 0; index < length; index++) {
				boolean stringInstance = s.getUByte() == 1;
				int key = s.getUInt24();
				Object value = stringInstance ? s.getString() : s.getInt();
				clientScriptData.put(key, value);
			}

			parameters = new LinkedHashMap<Parameter, Object>();
			for (Parameter parameter : Parameter.values()) {
				if (clientScriptData.containsKey(parameter.value())) {
					parameters.put(parameter, clientScriptData.get(parameter.value()));
				}
			}

			unknowns = new LinkedHashMap<Integer, Object>(clientScriptData);
			for (Parameter parameter : parameters.keySet()) {
				unknowns.remove(parameter.value());
			}
			if (unknowns.isEmpty()) {
				unknowns = null;
			}
		} else {
			System.out.println("unhandled " + opcode);
		}
	}

	public void fix() {
		if (noteTemplateId != -1) {
			fixNoted(loader.load(noteTemplateId), loader.load(noteId));
		}
//		if (lentTemplateId != -1 && lentId != -1) {
//			fixLent(loader.load(lentTemplateId), loader.load(lentId));
//		}
//		if (cosmeticTemplateId != -1) {
//			fixCosmetic(loader.load(cosmeticTemplateId), loader.load(cosmeticId));
//		}
	}

	public void fixLent(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, "Discard");
		lent = true;
	}

	public void fixCosmetic(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, "Destroy");
		cosmetic = true;
	}

	public void fixNoted(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, null);
		noted = true;
	}

	void fix(ItemDefinition lhs, ItemDefinition rhs, String action4) {
//		this.unknown1 = lhs.unknown1;
//		unknown4 = lhs.unknown4;
//		unknown5 = lhs.unknown5;
//		unknown6 = lhs.unknown6;
//		unknown95 = lhs.unknown95;
//		unknown7 = lhs.unknown7;
//		unknown8 = lhs.unknown8;
		boolean bool = action4 == null;
//		ItemDefinition itemDefinition_210_ = bool ? lhs : rhs;
//		this.unknown40_0 = itemDefinition_210_.unknown40_0;
//		this.unknown40_1 = itemDefinition_210_.unknown40_1;
//		this.unknown42 = itemDefinition_210_.unknown42;
//		this.unknown41_0 = itemDefinition_210_.unknown41_0;
//		this.unknown41_1 = itemDefinition_210_.unknown41_1;
		name = rhs.name;
		members = rhs.members;
		if (bool) {
			value = rhs.value;
			stackOffset = 1;
		} else {
			value = 0;
			stackOffset = rhs.stackOffset;
			slot = rhs.slot;
			equipmentType = rhs.equipmentType;
//			unknown27 = rhs.unknown27;
//			this.unknown23 = rhs.unknown23;
//			this.unknown24 = rhs.unknown24;
//			this.unknown78 = rhs.unknown78;
//			this.unknown25 = rhs.unknown25;
//			this.unknown26 = rhs.unknown26;
//			this.unknown79 = rhs.unknown79;
//			this.unknown125_0 = rhs.unknown125_0;
//			this.unknown126_0 = rhs.unknown126_0;
//			this.unknown125_1 = rhs.unknown125_1;
//			this.unknown126_1 = rhs.unknown126_1;
//			this.unknown125_2 = rhs.unknown125_2;
//			this.unknown126_2 = rhs.unknown126_2;
//			this.unknown90 = rhs.unknown90;
//			this.unknown92 = rhs.unknown92;
//			this.unknown91 = rhs.unknown91;
//			this.unknown93 = rhs.unknown93;
			categoryId = rhs.categoryId;
			team = rhs.team;
			groundActions = rhs.groundActions;
			copyParams(rhs);
			actions = new String[5];
			if (null != rhs.actions) {
				System.arraycopy(rhs.actions, 0, actions, 0, 4);
			}
			actions[4] = action4;
		}
	}

	private void copyParams(ItemDefinition from) {
		clientScriptData = from.clientScriptData;
		parameters = from.parameters;
		unknowns = from.unknowns;
	}

	public ItemDefinition unnoted() {
		if (noted) {
			return loader.load(noteId);
		}
		return this;
	}
}
