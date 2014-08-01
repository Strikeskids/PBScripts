package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.ItemDefinitionLoader;
import com.sk.cache.wrappers.ProtocolWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 10:37
 */
public class ItemDefinition extends ProtocolWrapper {
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
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true), "members")}, 16).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "modelId")}, 1).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT, "value")}, 12).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "name")}, 2).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "noteId")}, 97).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UINT24)}, 23, 25).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 4, 5, 6, 7, 8, 24, 26, 78, 79, 90, 91, 92, 93, 95, 110, 111, 112).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true), "stackable")}, 11).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT, "noteTemplateId")}, 98).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "groundActions")}, 30, 31, 32, 33, 34).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "actions")}, 35, 36, 37, 38, 39).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "team")}, 115).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40, 41).addSelfToGroup(protocol);

	}

	public ItemDefinition(ItemDefinitionLoader loader, int id) {
		super(loader, id, protocol);
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
}