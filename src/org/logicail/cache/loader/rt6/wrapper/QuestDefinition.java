package org.logicail.cache.loader.rt6.wrapper;

import com.sk.cache.wrappers.StreamedWrapper;
import com.sk.cache.wrappers.protocol.BasicProtocol;
import com.sk.cache.wrappers.protocol.ExtraAttributeReader;
import com.sk.cache.wrappers.protocol.ProtocolGroup;
import com.sk.cache.wrappers.protocol.extractor.*;
import com.sk.datastream.Stream;
import org.logicail.cache.loader.rt6.wrapper.loaders.QuestDefinitionLoader;
import org.powerbot.script.rt6.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/08/2014
 * Time: 20:12
 */
public class QuestDefinition extends StreamedWrapper {
	private static final ProtocolGroup protocol = new ProtocolGroup();

	public String name;
	public int[] scriptId, scriptStartValue, scriptEndValue, configId, configStartValue, configEndValue;

	public QuestDefinition(QuestDefinitionLoader loader, int id) {
		super(loader, id);
	}

	/**
	 * Has the current user complated this quest?
	 *
	 * @param ctx
	 * @return <code>true</code> if the user has completed the quest, otherwise <code>false</code>
	 */
	public boolean complete(ClientContext ctx) {
		if (scriptId != null) {
			for (int i = 0; i < scriptId.length; i++) {
				int scripts = scriptId[i];

			}
		} else if (configId != null) {

		}

		return false;
	}

	static {
		new ExtraAttributeReader().addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.USHORT}, null)}, 13).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.BIG_SMART)}, 17).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 6, 7).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.INT}, null)}, 10).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.USHORT)}, 5, 15).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.INT)}, 12).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.UBYTE, ParseType.UBYTE}, null)}, 14).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(new StaticExtractor(true))}, 8).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE)}, 9).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.JAG_STRING)}, 2).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new ArrayExtractor(ParseType.UBYTE, 0, new StreamExtractor[]{ParseType.INT, ParseType.INT, ParseType.INT, ParseType.STRING}, null)}, 18, 19).addSelfToGroup(protocol);
	}

	@Override
	public void decode(Stream s) {
		int opcode;
		while ((opcode = s.getUByte()) != 0) {
			if (opcode == 1) {
				name = s.getJagString();
			} else if (opcode == 3) {
				int count = s.getUByte();
				configId = new int[count];
				configStartValue = new int[count];
				configEndValue = new int[count];
				for (int i = 0; i < count; ++i) {
					configId[i] = s.getUShort();
					configStartValue[i] = s.getInt();
					configEndValue[i] = s.getInt();
				}
			} else if (opcode == 4) {
				int count = s.getUByte();
				scriptId = new int[count];
				scriptStartValue = new int[count];
				scriptEndValue = new int[count];
				for (int i = 0; i < count; ++i) {
					scriptId[i] = s.getUShort();
					scriptStartValue[i] = s.getInt();
					scriptEndValue[i] = s.getInt();
				}
			} else {
				protocol.read(this, opcode, s);
			}
		}
	}
}
