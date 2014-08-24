package org.logicail.cache.loader.rt6.wrapper;

import org.logicail.cache.loader.rt6.wrapper.loaders.ItemDefinitionLoader;
import org.logicail.cache.loader.rt6.wrapper.requirements.*;
import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:32
 */
public class ItemDefinition extends StreamedWrapper {
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
	public Slot slotEnum = null;
	public boolean tradable;
	public int equipmentType = -1;
	public int lentId = -1;
	public int categoryId;
	public int cosmeticId = -1;
	public int cosmeticTemplateId = -1;
	public int lentTemplateId = -1;
	//public Category category = Category.MISCELLANEOUS;
	public String shardname = null;

	/**
	 * GE category
	 *
	 * @return
	 */
	public Category category() {
		loadParameters();
		return parameters != null && parameters.containsKey(Parameter.CATEGORY) ? Category.values()[(Integer) parameters.get(Parameter.CATEGORY)] : Category.MISCELLANEOUS;
	}

	// All unedited 249 data
	public LinkedHashMap<Integer, Object> clientScriptData = new LinkedHashMap<Integer, Object>();
	private AtomicBoolean loaded = new AtomicBoolean();

	private void loadParameters() {
		if (loaded.get()) {
			return;
		}

		loaded.set(true);

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
	}

	private LinkedHashMap<Parameter, Object> parameters;

	public LinkedHashMap<Parameter, Object> parameters() {
		loadParameters();
		return parameters;
	}

	public List<String> wornOptions() {
		return ItemParameter.WORN_OPTIONS.get(clientScriptData);
	}

	public List<SkillRequirement> skillRequirements() {
		return ItemParameter.SKILL_REQUIREMENT.get(clientScriptData);
	}

	public ItemRequirement tool() {
		return ItemParameter.TOOL.get(clientScriptData);
	}

	public List<CombatSkillRequirement> wieldRequirements() {
		return ItemParameter.WIELD_REQUIREMENT.get(clientScriptData);
	}

//	public Requirement[] combatLevelRequirement() {
//		return ItemParameter.Equipment.COMBAT_LEVEL_REQUIREMENT.get(clientScriptData);
//	}

	public ItemParameter.EquipmentType equipmentTypeParam() {
		return ItemParameter.EquipmentType.getType(clientScriptData);
	}

	public CreationSkillRequirement creationSkill() {
		return ItemParameter.CREATION_SKILL.get(clientScriptData);
	}

	public List<ItemRequirement> resourceRequirement() {
		return ItemParameter.RESOURCE_REQUIREMENT.get(clientScriptData);
	}

	private LinkedHashMap<Integer, Object> unknowns;

	public LinkedHashMap<Integer, Object> unknowns() {
		loadParameters();
		return unknowns;
	}


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


	public void fixNoted(ItemDefinition lhs, ItemDefinition rhs) {
		fix(lhs, rhs, null);
		noted = true;
	}


	public ItemDefinition(ItemDefinitionLoader loader, int id) {
		super(loader, id);
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

	public boolean hasAnyParameter(Parameter... parameters) {
		if (this.parameters == null) {
			return false;
		}

		for (Parameter parameter : parameters) {
			if (this.parameters.containsKey(parameter)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasParameter(Parameter parameter) {
		if (parameters != null && parameters.containsKey(parameter)) {
			final Object v = parameters.get(parameter);
			if (v instanceof Integer) {
				return (Integer) v > 0;
			} else {
				return true;
			}
		}
		return false;
	}

	static {
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.SHORT, ParseType.SHORT}, null)}, 41).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART)}, 1, 23, 24, 25, 26, 78, 79, 90, 91, 92, 93).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(false))}, 156).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.BYTE}, null)}, 42).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 113, 114).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 132).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 96).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 27, 134).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 4, 5, 6, 7, 8, 18, 44, 45, 95, 110, 111, 112, /*139, 140,*/ 142, 143, 144, 145, 146, 150, 151, 152, 153, 154, 161, 162, 163).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE)}, 125, 126).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT)}, 43).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true))}, 157).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(2))}, 165).addSelfToGroup(protocol);
	}

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 13) {
				slot = s.getUByte();
				slotEnum = Slot.get(slot);
				continue;
			}
			if (opcode == 249) {
				int length = s.getUByte();
				clientScriptData = new LinkedHashMap<Integer, Object>(length);
				for (int index = 0; index < length; index++) {
					boolean stringInstance = s.getUByte() == 1;
					int key = s.getUInt24();
					Object value = stringInstance ? s.getString() : s.getInt();
					clientScriptData.put(key, value);
				}
				continue;
			}
			if (opcode == 98) {
				noteTemplateId = s.getUShort();
				continue;
			}
			if (opcode == 1) {
				modelId = s.getBigSmart();
				continue;
			}
			if (opcode == 16) {
				members = true;
				continue;
			}
			if (opcode == 97) {
				noteId = s.getUShort();
				continue;
			}
			if (opcode == 11) {
				stackOffset = 1;
				continue;
			}
			if (opcode >= 30 && opcode <= 34) {
				groundActions[opcode - 30] = s.getString();
				continue;
			}
			if (opcode == 122) {
				lentTemplateId = s.getUShort();
				continue;
			}
			if (opcode == 164) {
				shardname = s.getString();
				continue;
			}
			if (opcode == 121) {
				lentId = s.getUShort();
				continue;
			}
			if (opcode == 14) {
				equipmentType = s.getUByte();
				continue;
			}
			if (opcode >= 35 && opcode <= 39) {
				actions[opcode - 35] = s.getString();
				continue;
			}
			if (opcode == 94) {
				categoryId = s.getUShort();
				continue;
			}
			if (opcode == 139) {
				cosmeticId = s.getUShort();
				continue;
			}
			if (opcode == 140) {
				cosmeticTemplateId = s.getUShort();
				continue;
			}
			if (opcode == 2) {
				name = s.getString();
				continue;
			}
			if (opcode == 65) {
				tradable = true;
				continue;
			}
			if (opcode == 12) {
				value = s.getInt();
				continue;
			}
			if (opcode == 115) {
				team = s.getUByte();
				continue;
			}

			protocol.read(this, opcode, s);
		}
	}
}
