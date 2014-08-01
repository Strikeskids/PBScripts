package com.logicail.loader.rt6.wrapper;

import com.logicail.loader.rt6.wrapper.loaders.ItemDefinitionLoader;
import com.sk.cache.wrappers.ProtocolWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.StaticLocReader;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:32
 */
public class ItemDefinition extends ProtocolWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();
	public String name = null;
	public boolean noted;
	public boolean lent;
	public boolean cosmetic;
	public boolean members;
	public String[] groundActions = new String[]{null, null, "Take", null, null};
	public int noteId = -1;
	public String[] actions = new String[]{null, null, null, null, "Drop"};
	public int noteTemplateId = -1;
	public int modelId = -1;
	public int[] stackSizes;
	public int team = 0;
	public int stackOffset = 0;
	public int value = 1;
	public int[] stackVarient;
	public int slot = -1;
	public boolean tradable;
	public int equipmentType = -1;
	public int lentId = -1;
	public int categoryId;
	public int cosmeticId = -1;
	public int cosmeticTemplateId = -1;
	public int lentTemplateId = -1;
	public String shardname = null;
	public HashMap<Parameter, Object> parameters;
	private final ItemDefinitionLoader loader;

	public void fix() {
		if (noteTemplateId != -1) {
			fixNoted(loader.load(noteTemplateId), loader.load(noteId));
		}
		if (lentTemplateId != -1 && lentId != -1) {
			fixLent(loader.load(lentTemplateId), loader.load(lentId));
		}
		if (cosmeticTemplateId != -1) {
			fixCosmetic(loader.load(cosmeticTemplateId), loader.load(cosmeticId));
		}
	}

	public void fixCosmetic(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, "Destroy");
		cosmetic = true;
	}

	public void fixLent(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, "Discard");
		lent = true;
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
			parameters = rhs.parameters;
			actions = new String[5];
			if (null != rhs.actions) {
				System.arraycopy(rhs.actions, 0, actions, 0, 4);
			}
			actions[4] = action4;
		}
	}

	public void fixNoted(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, null);
		noted = true;
	}

	static {
		new StaticLocReader(249) {
			@Override
			public void read(Object obj, int type, Stream stream) {
				int length = stream.getUByte();

				HashMap<Integer, Object> parameters = new LinkedHashMap<Integer, Object>(length);

				for (int index = 0; index < length; index++) {
					boolean stringInstance = stream.getUByte() == 1;
					int key = stream.getUInt24();
					Object value = stringInstance ? stream.getString() : stream.getInt();
					parameters.put(key, value);
				}

				FieldExtractor.setValue(obj, type, type, "parameters", parameters);
			}
		}.addSelfToGroup(protocol);

		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.SHORT, ParseType.SHORT}, null)}, 41).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "noteTemplateId")}, 98).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART, "modelId")}, 1).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART)}, 1, 23, 24, 25, 26, 78, 79, 90, 91, 92, 93).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(false))}, 156).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.BYTE}, null)}, 42).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true), "members")}, 16).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "noteId")}, 97).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(1), "stackOffset")}, 11).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "groundActions")}, 30, 31, 32, 33, 34).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 113, 114).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "lentTemplateId")}, 122).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "slot")}, 13).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 132).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "shardname")}, 164).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "lentId")}, 121).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "equipmentType")}, 14).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 96).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 27, 134).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "actions")}, 35, 36, 37, 38, 39).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "categoryId")}, 94).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "cosmeticId")}, 139).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "cosmeticTemplateId")}, 140).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 4, 5, 6, 7, 8, 18, 44, 45, 95, 110, 111, 112, /*139, 140,*/ 142, 143, 144, 145, 146, 150, 151, 152, 153, 154, 161, 162, 163).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE)}, 125, 126).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "name")}, 2).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT)}, 43).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true), "tradable")}, 65).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true))}, 157).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT, "value")}, 12).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "team")}, 115).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(2))}, 165).addSelfToGroup(protocol);
	}

	public ItemDefinition(ItemDefinitionLoader loader, int id) {
		super(loader, id, protocol);
		this.loader = loader;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemDefinition that = (ItemDefinition) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public ItemDefinition unnoted() {
		if (noted) {
			return loader.load(noteId);
		}
		return this;
	}
}