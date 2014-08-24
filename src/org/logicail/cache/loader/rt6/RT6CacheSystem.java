package org.logicail.cache.loader.rt6;

import com.sk.cache.fs.CacheSystem;
import org.logicail.cache.loader.rt6.wrapper.loaders.ItemDefinitionLoader;
import org.logicail.cache.loader.rt6.wrapper.loaders.ObjectDefinitionLoader;
import org.logicail.cache.loader.rt6.wrapper.loaders.QuestDefinitionLoader;
import org.logicail.cache.loader.rt6.wrapper.loaders.ScriptLoader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:20
 */
public class RT6CacheSystem extends CacheSystem {
	public final ItemDefinitionLoader itemLoader;
	public final ObjectDefinitionLoader objectLoader;
	public final QuestDefinitionLoader questLoader;
	public final ScriptLoader scriptLoader;

	public RT6CacheSystem(File cacheFolder) throws FileNotFoundException {
		super(cacheFolder);

		itemLoader = new ItemDefinitionLoader(this);
		objectLoader = new ObjectDefinitionLoader(this);
		questLoader = new QuestDefinitionLoader(this);
		scriptLoader = new ScriptLoader(this);
	}
}
