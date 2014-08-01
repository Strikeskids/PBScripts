package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.ObjectDefinitionLoader;
import com.logicail.protocol.ChildrenReader;
import com.sk.cache.wrappers.ProtocolWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.StaticLocReader;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.GameObject;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:49
 */
public class ObjectDefinition extends ProtocolWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();
	public String name = "null";
	public int type = 0;
	public int width = 1;
	public int height = 1;
	public String[] actions = new String[5];
	public int animationId = -1;
	public int[] modelTypes;
	public int[] modelIds;
	public int scriptId = -1;
	public int configId = -1;
	public int[] childrenIds;
	public boolean unwalkable = true;
	public int blockType = 0;
	public boolean solid = false;
	public int[] originalColors;
	public int[] modifiedColors;


	public static Filter<GameObject> name(final IClientContext ctx, final String name) {
		return new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject o) {
				final ObjectDefinition definition = ctx.definitions.get(o);
				return definition != null && definition.name != null && definition.name.equalsIgnoreCase(name);
			}
		};
	}

	static {
		new StaticLocReader(1) {
			@Override
			public void read(Object destination, int type, Stream s) {
				int count = s.getUByte();
				if (count > 0) {
					if (FieldExtractor.getValue(destination, "modelIds") == null /* || static boolean */) {
						int[] modelIds = new int[count];
						int[] modelTypes = new int[count];
						for (int i = 0; i < count; i++) {
							modelIds[i] = s.getUShort();
							modelTypes[i] = s.getUByte();
						}
						FieldExtractor.setValue(destination, type, type, "modelIds", modelIds);
						FieldExtractor.setValue(destination, type, type, "modelTypes", modelTypes);
					} else {
						s.skip(count * 3);
					}
				}
			}
		}.addSelfToGroup(protocol);

		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "name")}, 2).addSelfToGroup(protocol);

		new StaticLocReader(5) {
			@Override
			public void read(Object destination, int type, Stream s) {
				int count = s.getUByte();
				if (count > 0) {
					if (FieldExtractor.getValue(destination, "modelIds") == null /* || static boolean*/) {
						int[] modelIds = new int[count];
						for (int i = 0; i < count; i++) {
							modelIds[i] = s.getUShort();
						}
						FieldExtractor.setValue(destination, type, type, "modelIds", modelIds);
						FieldExtractor.setValue(destination, type, type, "modelTypes", null);
					} else {
						s.skip(count * 2);
					}
				}
			}
		}.addSelfToGroup(protocol);

		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(null))}, 21, 22, 23, 62, 64, 73).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 29, 39).addSelfToGroup(protocol);
		new ChildrenReader(77).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true), "solid")}, 74).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART, "animationId")}, 24).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, new String[]{"originalColors", "modifiedColors"})}, 40).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(1), "blockType")}, 27).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 28, 69, 75, 81).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 60, 65, 66, 67, 68, 70, 71, 72).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE), new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 79).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(false), "unwalkable")}, 18).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "width")}, 14).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "height")}, 15).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(false), "unwalkable"), new FieldExtractor(new StaticExtractor(0), "blockType")}, 17).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE)}, 78).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "actions")}, 30, 31, 32, 33, 34).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "type")}, 19).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 41).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.STRING, "name")}, 2).addSelfToGroup(protocol);
	}

	public ObjectDefinition(ObjectDefinitionLoader loader, int id) {
		super(loader, id, protocol);
	}

	public ObjectDefinition child(IClientContext ctx) {
		int index = 0;
		if (scriptId == -1) {
			index = configId != -1 ? ctx.varpbits.varpbit(configId) : -1;
		} else {
			final Script script = ctx.definitions.varp(scriptId);
			if (script != null) {
				index = ctx.varpbits.varpbit(script.configId) >> script.lowerBitIndex & Script.MASKS[script.upperBitIndex - script.lowerBitIndex];
			}
		}
		if (index >= 0 && index < childrenIds.length && childrenIds[index] != -1) {
			return ctx.definitions.object(childrenIds[index]);
		}
		return null;
	}

	public boolean hasChildren() {
		return childrenIds != null;
	}
}
