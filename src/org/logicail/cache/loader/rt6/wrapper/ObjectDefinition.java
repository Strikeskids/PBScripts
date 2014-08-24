package org.logicail.cache.loader.rt6.wrapper;

import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.loaders.WrapperLoader;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ExtraAttributeReader;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;
import org.logicail.rsbot.scripts.framework.context.rt6.IClientContext;

public class ObjectDefinition extends StreamedWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();

	public String name;
	public int type = -1;
	public int width = 1;
	public int height = 1;
	public boolean solid = false;
	public boolean unwalkable = true;
	public int blockType = 0;
	public String[] actions = new String[5];

	public int scriptId = -1;
	public int configId = -1;
	public int[] childrenIds;

	public int[] modelTypes;
	public int[][] modelIds;

	public ObjectDefinition(WrapperLoader<?> loader, int id) {
		super(loader, id);
	}

	public int childId(IClientContext ctx) {
		int index = -1;
		if (scriptId == -1) {
			index = configId != -1 ? ctx.varpbits.varpbit(configId) : -1;
		} else {
			final Script script = ctx.definitions.script(scriptId);
			if (script != null) {
				index = ctx.varpbits.varpbit(script.configId) >> script.lowerBitIndex & org.logicail.cache.loader.rt4.wrappers.Script.MASKS[script.upperBitIndex - script.lowerBitIndex];
			}
		}

		return index;
	}

	public ObjectDefinition child(IClientContext ctx) {
		if (childrenIds == null) {
			return this;
		}

		int index = childId(ctx);

		if (index >= 0 && index < childrenIds.length) {
			if (childrenIds[index] == -1) {
				return new ObjectDefinition(loader, -1);
			}

			return ctx.definitions.object(childrenIds[index]);
		}

		return this;
	}

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				int j = s.getUByte();
				modelTypes = new int[j];
				modelIds = new int[j][];

				for (int k = 0; k < j; k++) {
					modelTypes[k] = s.getByte();
					int d = s.getUByte();
					modelIds[k] = new int[d];
					for (int i = 0; i < d; i++) {
						modelIds[k][i] = s.getBigSmart();
					}
				}
			} else if (opcode == 77 || opcode == 92) {
				int script = s.getUShort();
				int config = s.getUShort();
				int ending = opcode == 92 ? s.getBigSmart() : -1;
				int count = s.getUByte() + 1;
				int[] arr = new int[count + 1];
				for (int i = 0; i < count; ++i)
					arr[i] = s.getBigSmart();
				arr[count] = ending;
				scriptId = script == 0xFFFF ? -1 : script;
				configId = config == 0xFFFF ? -1 : config;
				childrenIds = arr;
			} else if (opcode == 2) {
				name = s.getString();
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
			} else if (opcode == 27) {
				blockType = 1;
			} else if (opcode == 74) {
				solid = true;
			} else if (opcode >= 30 && opcode <= 34) {
				actions[opcode - 30] = s.getString();
			} else if (opcode >= 150 && opcode <= 154) {
				actions[opcode - 150] = s.getString();
			} else {
				protocol.read(this, opcode, s);
			}
		}
	}

	static {
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(null))}, 21, 22, 23, 62, 64, 73, 82, 88, 89, 91, 94, 97, 98, 103, 105, 168, 169, 177, 189, 198, 199, 200).addSelfToGroup(protocol);

		new ExtraAttributeReader().addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART)}, 24).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.BIG_SMART, ParseType.UBYTE}, null)}, 106).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.BYTE}, null)}, 42).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE), new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 79).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.UBYTE)}, 78).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE), new FieldExtractor(ParseType.USHORT)}, 99, 100).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE)}, 28, 29, 39, 196, 197).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 173).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 5, 160).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.SMART)}, 170, 171).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 69, 75, 81, 101, 104, 178, 186, 250, 251, 253, 254).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 44, 45, 65, 66, 67, 70, 71, 72, 93, 95, 102, 107, 164, 165, 166, 167, 190, 191, 192, 193, 194, 195).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT)}, 162).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE), new FieldExtractor(ParseType.BYTE)}, 163).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT, ParseType.USHORT}, null)}, 40, 41).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT), new FieldExtractor(ParseType.USHORT)}, 252, 255).addSelfToGroup(protocol);

	}
}
