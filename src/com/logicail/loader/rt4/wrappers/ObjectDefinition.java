package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.ObjectDefinitionLoader;
import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
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
public class ObjectDefinition extends StreamedWrapper {
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
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(null))}, 21, 22, 23, 62, 64, 73).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 29, 39).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 28, 69, 75, 81).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 60, 65, 66, 67, 68, 70, 71, 72).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE), new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 79).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE)}, 78).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 41).addSelfToGroup(protocol);
	}

	public ObjectDefinition(ObjectDefinitionLoader loader, int id) {
		super(loader, id);
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

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				int count = s.getUByte();
				if (modelIds == null) {
					modelIds = new int[count];
					modelTypes = new int[count];
					for (int i = 0; i < count; i++) {
						modelIds[i] = s.getUShort();
						modelTypes[i] = s.getUByte();
					}
				} else {
					s.skip(count * 3);
				}
			} else if (opcode == 2) {
				name = s.getString();
			} else if (opcode == 5) {
				int count = s.getUByte();
				if (count > 0) {
					if (modelIds == null) {
						modelIds = new int[count];
						for (int i = 0; i < count; i++) {
							modelIds[i] = s.getUShort();
						}
						modelTypes = null;
					} else {
						s.skip(count * 2);
					}
				}
			} else if (opcode == 14) {
				width = s.getUByte();
			} else if (opcode == 15) {
				height = s.getUByte();
			} else if (opcode == 17) {
				unwalkable = false;
				blockType = 0;
			} else if (opcode == 18) {
				unwalkable = false;
			} else if (opcode == 19) {
				type = s.getUByte();
			} else if (opcode == 24) {
				animationId = s.getBigSmart();
			} else if (opcode == 27) {
				blockType = 1;
			} else if (opcode >= 30 && opcode <= 34) {
				actions[opcode - 30] = s.getString();
			} else if (opcode == 40) {
				int count = s.getUByte();
				originalColors = new int[count];
				modifiedColors = new int[count];
				for (int i = 0; i < count; i++) {
					originalColors[i] = s.getUShort();
					modifiedColors[i] = s.getUShort();
				}
			} else if (opcode == 74) {
				solid = true;
			} else if (opcode == 77) {
				int script = s.getUShort();
				int config = s.getUShort();
				int count = s.getUByte();
				int[] children = new int[count + 1];
				for (int i = 0; i < children.length; i++) {
					children[i] = s.getUShort();
					if (children[i] == 0xffff) {
						children[i] = -1;
					}
				}
				scriptId = script == 0xFFFF ? -1 : script;
				configId = config == 0xFFFF ? -1 : config;
				childrenIds = children;
			} else {
				protocol.read(this, opcode, s);
			}
		}
	}
}
