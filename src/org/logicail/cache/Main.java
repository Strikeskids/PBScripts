package org.logicail.cache;

import org.logicail.cache.loader.rt6.RT6CacheSystem;
import com.sk.cache.wrappers.loaders.WrapperLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/07/2014
 * Time: 16:37
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final RT6CacheSystem system = new RT6CacheSystem(new File(System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "runescape" + File.separator + "LIVE" + File.separator));
//		print(system.itemLoader);
		print(system.objectLoader);
//		print(system.npcLoader);
//		print(system.varpLoader);

//		final RT4CacheSystem rt4 = new RT4CacheSystem(new File(System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator));
//		print(rt4.itemLoader);
//		print(rt4.objectLoader);
//		print(rt4.npcLoader);
//		print(rt4.varpLoader);
	}

	//	private static void print(ScriptDefinitionLoader loader) throws FileNotFoundException, NoSuchMethodException {
//
//		TreeMap<Integer, List<Script>> map = new TreeMap<Integer, List<Script>>(); // Just so don't have to sort keys
//
//		for (int i = 0; i < loader.size; i++) {
//			if (!loader.canLoad(i)) {
//				continue;
//			}
//
//			try {
//				final Script script = loader.load(i);
//				List<Script> list;
//				if (!map.containsKey(script.configId)) {
//					list = new LinkedList<Script>();
//					map.put(script.configId, list);
//				}
//				list = map.get(script.configId);
//				list.add(script);
//			} catch (IllegalArgumentException e) {
//				if (!e.getMessage().equals("Bad id") && !e.getMessage().equals("Empty")) {
//					throw e;
//				}
//			}
//		}
//		PrintWriter writer = null;
//		try {
//			final File output = new File("output" + File.separator + loader.getClass().getDeclaredMethod("load", int.class).getReturnType().getSimpleName().toLowerCase() + "-" + loader.version + ".txt");
//			writer = new PrintWriter(output);
//			writer.println("Version: " + loader.version);
//			writer.println("Generated: " + new Date().toString());
//			writer.println();
//
//			for (Integer key : map.keySet()) {
//				writer.println("[id=" + key + "]");
//
//				final List<Script> scripts = map.get(key);
//				for (Script script : scripts) {
//					writer.println("  [" + script.id + "] ctx.varpbits.varpbit(" + script.configId + ", " + script.lowerBitIndex + ", 0x" + Integer.toHexString(script.mask) + ")");
//				}
//				writer.println("");
//			}
//		} finally {
//			if (writer != null) {
//				writer.close();
//			}
//		}
//	}
//
	private static void print(WrapperLoader<?> loader) throws FileNotFoundException, NoSuchMethodException {
		PrintWriter writer = null;
		try {
			final File output = new File("output" + File.separator + loader.getClass().getDeclaredMethod("load", int.class).getReturnType().getSimpleName().toLowerCase() + "-" + /*loader.version +*/ ".txt");
			writer = new PrintWriter(output);
			//writer.println("Version: " + loader.version);
			writer.println("Generated: " + new Date().toString());
			writer.println();

			for (int i = 0; i < 60000; i++) {
				if (!loader.canLoad(i)) {
					continue;
				}

				try {
					writer.println(loader.load(i));
				} catch (IllegalArgumentException e) {
					if (!e.getMessage().equals("Bad id") && !e.getMessage().equals("Empty")) {
						throw e;
					}
				}
			}

		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
//
//	private static void print(ArchiveLoader<?> loader) throws FileNotFoundException, NoSuchMethodException {
//		PrintWriter writer = null;
//		try {
//			final File output = new File("output" + File.separator + loader.getClass().getDeclaredMethod("load", int.class).getReturnType().getSimpleName().toLowerCase() + "-" + loader.version + ".txt");
//			writer = new PrintWriter(output);
//			writer.println("Version: " + loader.version);
//			writer.println("Generated: " + new Date().toString());
//			writer.println();
//
//			for (int i = 0; i < loader.size; i++) {
//				if (!loader.canLoad(i)) {
//					continue;
//				}
//				try {
//					writer.println(loader.load(i));
//				} catch (IllegalArgumentException e) {
//					if (!e.getMessage().equals("Bad id") && !e.getMessage().equals("Empty")) {
//						throw e;
//					}
//				}
//			}
//		} finally {
//			if (writer != null) {
//				writer.close();
//			}
//		}
//	}
}
