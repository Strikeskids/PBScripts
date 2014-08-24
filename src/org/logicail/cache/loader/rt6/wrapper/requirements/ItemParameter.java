package org.logicail.cache.loader.rt6.wrapper.requirements;

import org.logicail.cache.loader.rt6.wrapper.Parameter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: Tom
 * Date: 03/01/14
 * Time: 20:26
 */
public class ItemParameter {
	public static final ListRequirement<SkillRequirement> SKILL_REQUIREMENT = new ListRequirement<SkillRequirement>() {
		@Override
		public List<SkillRequirement> get(Map<Integer, Object> params) {
			LinkedList<SkillRequirement> requirements = new LinkedList<SkillRequirement>();

			Map<Parameter, Parameter> pairs = new HashMap<Parameter, Parameter>() {{
				put(Parameter.SKILL_1, Parameter.LEVEL_1);
				put(Parameter.SKILL_2, Parameter.LEVEL_2);
				put(Parameter.SKILL_3, Parameter.LEVEL_3);
				put(Parameter.SKILL_4, Parameter.LEVEL_4);
				//put(Parameter.SKILL_5, Parameter.LEVEL_5);
			}};

			for (Map.Entry<Parameter, Parameter> entry : pairs.entrySet()) {
				if (params.containsKey(entry.getKey().value()) && params.containsKey(entry.getValue().value())) {
					requirements.add(new SkillRequirement((Integer) params.get(entry.getKey().value()), (Integer) params.get(entry.getValue().value())));
				}
			}

			return requirements;
		}
	};

	public static final ListRequirement<String> WORN_OPTIONS = new ListRequirement<String>() {
		@Override
		public List<String> get(Map<Integer, Object> params) {
			LinkedList<String> strings = new LinkedList<String>();
			List<Parameter> pairs = new LinkedList<Parameter>() {{
				add(Parameter.EQUIP_ACTION_1);
				add(Parameter.EQUIP_ACTION_2);
				add(Parameter.EQUIP_ACTION_3);
				add(Parameter.EQUIP_ACTION_4);
				add(Parameter.EQUIP_ACTION_5);
			}};

			for (Parameter value : pairs) {
				if (params.containsKey(value.value())) {
					strings.add(String.valueOf(params.get(value.value())));
				} else {
					break;
				}
			}
			return strings;
		}
	};

	public static final Requirement<CreationSkillRequirement> CREATION_SKILL = new Requirement<CreationSkillRequirement>() {
		@Override
		public CreationSkillRequirement get(Map<Integer, Object> params) {
			if (params.containsKey(Parameter.CREATION_SKILL.value()) && params.containsKey(Parameter.CREATION_SKILL_LEVEL.value())) {
				return new CreationSkillRequirement((Integer) params.get(Parameter.CREATION_SKILL.value()), (Integer) params.get(Parameter.CREATION_SKILL_LEVEL.value()));
			}

			return null;
		}
	};

	public static final Requirement<ItemRequirement> TOOL = new Requirement<ItemRequirement>() {
		@Override
		public ItemRequirement get(Map<Integer, Object> params) {
			if (params.containsKey(Parameter.TOOL.value())) {
				return new ItemRequirement((Integer) params.get(Parameter.TOOL.value()), 1);
			}

			return null;
		}
	};

	public static final ListRequirement<ItemRequirement> RESOURCE_REQUIREMENT = new ListRequirement<ItemRequirement>() {
		@Override
		public List<ItemRequirement> get(Map<Integer, Object> params) {
			final LinkedList<ItemRequirement> result = new LinkedList<ItemRequirement>();

			Map<Parameter, Parameter> pairs = new HashMap<Parameter, Parameter>() {{
				put(Parameter.INGREDIENT_ID_1, Parameter.INGREDIENT_COUNT_1);
				put(Parameter.INGREDIENT_ID_2, Parameter.INGREDIENT_COUNT_2);
				put(Parameter.INGREDIENT_ID_3, Parameter.INGREDIENT_COUNT_3);
				put(Parameter.INGREDIENT_ID_4, Parameter.INGREDIENT_COUNT_4);
				put(Parameter.INGREDIENT_ID_5, Parameter.INGREDIENT_COUNT_5);
				put(Parameter.INGREDIENT_ID_6, Parameter.INGREDIENT_COUNT_6);
			}};

			for (Map.Entry<Parameter, Parameter> entry : pairs.entrySet()) {
				if (params.containsKey(entry.getKey().value()) && params.containsKey(entry.getValue().value())) {
					result.add(new ItemRequirement((Integer) params.get(entry.getKey().value()), (Integer) params.get(entry.getValue().value())));
				}
			}

			return result;
		}
	};

	public static final ListRequirement<CombatSkillRequirement> WIELD_REQUIREMENT = new ListRequirement<CombatSkillRequirement>() {
		@Override
		public List<CombatSkillRequirement> get(Map<Integer, Object> params) {
			LinkedList<CombatSkillRequirement> result = new LinkedList<CombatSkillRequirement>();
			Map<Parameter, Parameter> pairs = new HashMap<Parameter, Parameter>() {{
				put(Parameter.WIELD_SKILL_1, Parameter.WIELD_LEVEL_1);
				put(Parameter.WIELD_SKILL_2, Parameter.WIELD_LEVEL_2);
				put(Parameter.WIELD_SKILL_3, Parameter.WIELD_LEVEL_3);
				put(Parameter.WIELD_SKILL_4, Parameter.WIELD_LEVEL_4);
				put(Parameter.WIELD_SKILL_5, Parameter.WIELD_LEVEL_5);
				put(Parameter.WIELD_SKILL_6, Parameter.WIELD_LEVEL_6);
			}};

			for (Map.Entry<Parameter, Parameter> entry : pairs.entrySet()) {
				if (params.containsKey(entry.getKey().value()) && params.containsKey(entry.getValue().value())) {
					result.add(new CombatSkillRequirement((Integer) params.get(entry.getKey().value()), (Integer) params.get(entry.getValue().value())));
				}
			}

			return result;
		}
	};

//	public static enum Equipment implements ListRequirement<CombatSkillRequirement> {
//		/*
//		COMBAT_LEVEL_REQUIREMENT() {
//			@Override
//			public CombatLevelRequirement[] get(Map<Integer, Object> params) {
//				if (params.containsKey(Parameter.COMBAT_LEVEL_REQUIREMENT.value())) {
//					return new CombatLevelRequirement[]{new CombatLevelRequirement((int) params.get(Parameter.COMBAT_LEVEL_REQUIREMENT.value()))};
//				}
//				return null;
//			}
//		}*/
//	}

	public static enum EquipmentType implements Requirement<Boolean> {
		MELEE_GEAR(2821),
		RANGE_GEAR(2822),
		MAGIC_GEAR(2823),
		NEUTRAL_GEAR(2824),
		MELEE_WEAPON(2825),
		RANGE_WEAPON(2826),
		MAGIC_WEAPON(2827),
		SHIELD_SLOT(2832);

		private final int index;

		private EquipmentType(int index) {
			this.index = index;
		}

		@Override
		public Boolean get(Map<Integer, Object> params) {
			return params.containsKey(index);
		}

		public static EquipmentType getType(Map params) {
			for (EquipmentType type : values()) {
				if (type.get(params)) {
					return type;
				}
			}
			return null;
		}
	}

	public static enum OffensiveStat implements Requirement<Integer> {
		ATTACK_SPEED(14),
		MELEE_DAMAGE(641, 10),
		MELEE_ACCURACY(3267),
		MAGIC_ACCURACY(3),
		RANGED_DAMAGE(643, 10),
		RANGE_ACCURACY(4),
		MELEE_CRITICAL(2833, 10),
		RANGE_CRITICAL(2834, 10),
		MAGIC_CRITICAL(2835, 10);

		private final int index;
		private final int divisor;

		private OffensiveStat(int index, int divisor) {
			this.index = index;
			this.divisor = divisor;
		}

		private OffensiveStat(final int index) {
			this(index, 1);
		}

		@Override
		public Integer get(Map<Integer, Object> params) {
			return params.containsKey(index) ? Integer.parseInt(params.get(index).toString()) / divisor : -1;
		}

		public String getData(Map param) {
			return name() + ": " + get(param);
		}
	}

	public static enum DefensiveStat implements Requirement<Integer> {
		HITPOINTS(1326),
		MELEE_ARMOUR_BIAS(2866),
		MAGIC_ARMOUR_BIAS(2867),
		RANGE_ARMOUR_BIAS(2868),
		ARMOUR_RATING(2870, 10),
		PRAYER_BONUS(2946);

		private final int index;
		private final int divisor;

		private DefensiveStat(int index, int divisor) {
			this.index = index;
			this.divisor = divisor;
		}

		private DefensiveStat(final int index) {
			this(index, 1);
		}

		@Override
		public Integer get(Map<Integer, Object> params) {
			return params.containsKey(index) ? Integer.parseInt(params.get(index).toString()) / divisor : -1;
		}

		public String getData(Map param) {
			return name() + ": " + get(param);
		}
	}
}
