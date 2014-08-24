package org.logicail.cache.loader.rt6.wrapper.loaders;

import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.sk.cache.wrappers.loaders.WrapperLoader;
import org.logicail.cache.loader.rt6.wrapper.QuestDefinition;

import java.util.HashMap;
import java.util.Map;

public class QuestDefinitionLoader extends WrapperLoader<QuestDefinition> {
	private final Archive source;
	private Map<String, QuestDefinition> map = new HashMap<String, QuestDefinition>();

	public QuestDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem);
		source = cacheSystem.getCacheSource().getCacheType(2).getArchive(35);
	}

	@Override
	public QuestDefinition load(int id) {
		FileData data = source.getFile(id);
		if (data == null)
			throw new IllegalArgumentException("Bad quest id");
		QuestDefinition ret = new QuestDefinition(this, id);
		ret.decode(data.getDataAsStream());
		return ret;
	}

	public QuestDefinition find(String name) {
		//Fairy Tale I - Growing Pains
		if (map.isEmpty()) {
			int i = 0;
			while (canLoad(i)) {
				final QuestDefinition definition = load(i);
				map.put(definition.name, definition);
				i++;
			}
		}
		if (!map.containsKey(name)) {
			throw new IllegalArgumentException("Bad quest name");
		}
		return map.get(name);
	}

	@Override
	public boolean canLoad(int id) {
		return source.getFile(id) != null;
	}

}
