package com.logicail.loader.rt6;

import com.sk.cache.fs.CacheSystem;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 20/07/2014
 * Time: 12:20
 */
public class RT6CacheSystem extends CacheSystem {
	public ItemDefinitionLoader itemLoader;

	public RT6CacheSystem(File cacheFolder) throws FileNotFoundException {
		super(cacheFolder);

		itemLoader = new ItemDefinitionLoader(this);
	}
}
