package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.ItemDefinitionLoader;
import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.ArrayExtractor;
import com.sk.cache.wrappers.protocol.extractor.FieldExtractor;
import com.sk.cache.wrappers.protocol.extractor.ParseType;
import com.sk.cache.wrappers.protocol.extractor.StreamExtractor;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 10:37
 */
public class ItemDefinition extends StreamedWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();
	public int team = 0;
	public String name = null;
	public boolean noted;
	public boolean members;
	public String[] actions = new String[]{null, null, null, null, "Drop"};
	public String[] groundActions = new String[]{null, null, "Take", null, null};
	public int noteId = -1;
	public int noteTemplateId = -1;
	public int modelId = -1;
	public boolean stackable;
	public int value = 1;
	private ItemDefinitionLoader loader;

	static {
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 113, 114).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UINT24)}, 23, 25).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 4, 5, 6, 7, 8, 24, 26, 78, 79, 90, 91, 92, 93, 95, 110, 111, 112).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40, 41).addSelfToGroup(protocol);

	}

	public ItemDefinition(ItemDefinitionLoader loader, int id) {
		super(loader, id);
		this.loader = loader;
	}

	public void fix() {
		if (noteTemplateId > -1) {
			noted = true;
			fix(loader.load(noteTemplateId), loader.load(noteId));
		}
	}

	private void fix(ItemDefinition lhs, ItemDefinition rhs) {
		this.modelId = lhs.modelId;
		this.name = rhs.name;
		this.members = rhs.members;
		this.value = rhs.value;
		this.stackable = true;
	}

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 16) {
				members = true;
			} else if (opcode == 1) {
				modelId = s.getUShort();
			} else if (opcode == 12) {
				value = s.getInt();
			} else if (opcode == 2) {
				name = s.getString();
			} else if (opcode == 97) {
				noteId = s.getUShort();
			} else if (opcode == 11) {
				stackable = true;
			} else if (opcode == 98) {
				noteTemplateId = s.getUShort();
			} else if (opcode >= 30 && opcode <= 34) {
				groundActions[opcode - 30] = s.getString();
			} else if (opcode >= 35 && opcode <= 39) {
				actions[opcode - 35] = s.getString();
			} else if (opcode == 115) {
				team = s.getUByte();
			} else {
				protocol.read(this, opcode, s);
			}
		}
	}
}