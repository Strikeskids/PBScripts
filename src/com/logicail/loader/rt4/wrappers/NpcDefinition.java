package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.NpcDefinitionLoader;
import com.logicail.protocol.ChildrenReader;
import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;
import org.logicail.rsbot.scripts.framework.context.rt4.RT4ClientContext;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Npc;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/07/2014
 * Time: 18:00
 */
public class NpcDefinition extends StreamedWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();
	public String name = "null";
	public int combatLevel = -1;
	public String[] actions = new String[5];
	public int idleAnimation = -1;
	public int[] modelIds;
	public int scriptId = -1;
	public int configId = -1;
	public int[] childrenIds;
	public boolean visible = false;
	public boolean clickable = true;
	public boolean drawMiniMapDot = true;

	static {
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 100, 101).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 60).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(false))}, 109, 111).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 12).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 13, 14, 15, 16, 97, 98, 102, 103).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 17).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40, 41).addSelfToGroup(protocol);
	}

	public static Filter<Npc> name(final RT4ClientContext ctx, final String name) {
		return new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				final NpcDefinition definition = ctx.definitions.get(npc);
				return definition != null && definition.name != null && definition.name.equalsIgnoreCase(name);
			}
		};
	}

	public NpcDefinition(NpcDefinitionLoader loader, int id) {
		super(loader, id);
	}

	public NpcDefinition child(RT4ClientContext ctx) {
		int index = 0;
		if (scriptId == -1) {
			index = configId != -1 ? ctx.varpbits.varpbit(configId) : -1;
		} else {
			final Script script = ctx.definitions.varp(scriptId);
			if (script != null) {
				index = script.execute(ctx);
			}
		}
		if (index >= 0 && index < childrenIds.length && childrenIds[index] != -1) {
			return ctx.definitions.npc(childrenIds[index]);
		}
		return null;
	}

	@Override
	public void decode(Stream s) {

		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				final int count = s.getUByte();
				modelIds = new int[count];
				for (int i = 0; i < count; ++i) {
					modelIds[i] = s.getUShort();
				}
			} else if (opcode == 2) {
				name = s.getString();
			} else if (opcode >= 30 && opcode <= 34) {
				actions[opcode - 30] = s.getString();
			} else if (opcode == 93) {
				drawMiniMapDot = false;
			} else if (opcode == 95) {
				combatLevel = s.getUShort();
			} else if (opcode == 99) {
				visible = true;
			} else if (opcode == 107) {
				clickable = false;
			} else if (opcode == 106) {
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

		new ChildrenReader(106).addSelfToGroup(protocol);
	}
}
