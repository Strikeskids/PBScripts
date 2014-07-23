package com.logicail.loader.rt4.wrappers;

import com.logicail.loader.rt4.wrappers.loaders.WrapperLoader;
import com.sk.datastream.Stream;

public abstract class StreamedWrapper extends Wrapper {

	public StreamedWrapper(WrapperLoader<?> loader, int id) {
		super(loader, id);
	}

	public abstract void decode(Stream stream);

}
