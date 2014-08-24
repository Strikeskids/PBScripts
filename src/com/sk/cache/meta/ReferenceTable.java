package com.sk.cache.meta;

import com.sk.cache.fs.CacheSource;
import com.sk.datastream.ByteStream;
import com.sk.datastream.Stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReferenceTable {

	private static final int MINIMUM_FORMAT_FOR_VERSION = 6;
	private static final int FLAG_IDENTIFIERS = 0x1, FLAG_WHIRLPOOL = 0x2, FLAG_UNKNOWN_1 = 0x4, FLAG_UNKNOWN_2 = 0x8;
	private final CacheSource cache;
	private final int id;
	private final boolean rs3;

	private int flags;
	private int version;
	private int format;

	private Map<Integer, ArchiveMeta> entries;

	private Stream data;
	private int[] ids;
	private int[][] children;

	public ReferenceTable(CacheSource cache, int id, boolean rs3) {
		this.cache = cache;
		this.id = id;
		this.rs3 = rs3;
	}

	public int getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public int getFlags() {
		return flags;
	}

	public int getFormat() {
		return format;
	}

	public ArchiveMeta getEntry(int id) {
		return entries.get(id);
	}

	public void init() throws IOException {
		if (entries == null) {
			synchronized (this) {
				if (entries == null) {
					entries = new HashMap<Integer, ArchiveMeta>();
					ByteStream compressed = new ByteStream(getTableData());
					byte[] data = compressed.decompress();
					decode(new ByteStream(data));
				}
			}
		}
	}

	private ArchiveRequest getQuery() {
		return cache.getMetaIndex().getArchiveMeta(id);
	}

	private byte[] getTableData() {
		ArchiveRequest query = getQuery();
		return cache.getSourceSystem().readArchive(query);
	}

	private void decode(Stream data) {
		this.data = data;
		entries = new HashMap<Integer, ArchiveMeta>();
		decode();
		this.data = null;
	}

	private void decode() {
		decodeFormat();
		if (hasVersion())
			decodeVersion();
		decodeFlags();
		decodeEntries();
	}

	private void decodeFormat() {
		format = data.getUByte();
	}

	private boolean hasVersion() {
		return format >= MINIMUM_FORMAT_FOR_VERSION;
	}

	private void decodeVersion() {
		version = data.getInt();
	}

	private void decodeFlags() {
		flags = data.getUByte();
	}

	private void decodeEntries() {
		int entryCount = data.getReferenceTableSmart();
		ids = getIds(entryCount);
		addEntries();
		if (hasIdentifiers())
			decodeEntryIdentifiers();
		decodeCrcs();
		if (rs3 && hasUnknown2())
			decodeUnknown2();
		if (hasWhirlpool())
			decodeWhirlpools();
		if (rs3 && hasUnknown1())
			decodeUnknown1();
		decodeVersions();
		decodeChildCounts();
		decodeChildren();
		ids = null;
	}

	private int[] getIds(int size) {
		int[] ids = new int[size];
		int accumulator = 0;
		for (int i = 0; i < ids.length; ++i) {
			int delta = data.getReferenceTableSmart();
			accumulator += delta;
			ids[i] = accumulator;
		}
		return ids;
	}

	private void addEntries() {
		for (int id : ids) {
			entries.put(id, new ArchiveMeta(id));
		}
	}

	private boolean hasIdentifiers() {
		return (flags & FLAG_IDENTIFIERS) == FLAG_IDENTIFIERS;
	}

	private void decodeEntryIdentifiers() {
		for (int id : ids) {
			entries.get(id).setIdentifier(data.getInt());
		}
	}

	private void decodeCrcs() {
		for (int id : ids) {
			entries.get(id).setCrc(data.getInt());
		}
	}

	private boolean hasWhirlpool() {
		return (flags & FLAG_WHIRLPOOL) == FLAG_WHIRLPOOL;
	}

	private void decodeWhirlpools() {
		for (int id : ids) {
			byte[] rawWhirlpool = entries.get(id).getAndInitializeWhirlpool();
			data.getBytes(rawWhirlpool);
		}
	}

	@SuppressWarnings("unused")
	private void decodeUnknown1() {
		for (int id : ids) {
			int value1 = data.getInt();
			int value2 = data.getInt();
		}
	}

	private boolean hasUnknown1() {
		return (flags & FLAG_UNKNOWN_1) == FLAG_UNKNOWN_1;
	}

	@SuppressWarnings("unused")
	private void decodeUnknown2() {
		for (int id : ids) {
			int value = data.getInt();
		}
	}

	private boolean hasUnknown2() {
		return (flags & FLAG_UNKNOWN_2) == FLAG_UNKNOWN_2;
	}

	private void decodeVersions() {
		for (int id : ids) {
			entries.get(id).setVersion(data.getInt());
		}
	}

	private void decodeChildCounts() {
		for (int id : ids) {
			int childCount = data.getReferenceTableSmart();
			entries.get(id).setChildCount(childCount);
		}
	}

	private void decodeChildren() {
		children = new int[ids.length][];
		for (int i = 0; i < ids.length; ++i) {
			ArchiveMeta currentEntry = getEntry(ids[i]);
			int childCount = currentEntry.getChildCount();
			children[i] = getIds(childCount);
			addEntryChildren(currentEntry, children[i]);
		}
		if (hasIdentifiers())
			decodeChildrenIdentifiers();
		children = null;
	}

	private void addEntryChildren(ArchiveMeta entry, int... ids) {
		for (int id : ids)
			entry.addChild(id);
	}

	private void decodeChildrenIdentifiers() {
		for (int i = 0; i < ids.length; ++i) {
			decodeChildIdentifiers(i, ids[i]);
		}
	}

	private void decodeChildIdentifiers(int index, int entryId) {
		for (int childId : children[index]) {
			entries.get(entryId).getChild(childId).setIdentifier(data.getInt());
		}
	}

	public Map<Integer, ArchiveMeta> getEntries() {
		return entries;
	}

}