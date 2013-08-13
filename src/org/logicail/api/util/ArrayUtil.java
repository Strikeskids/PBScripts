package org.logicail.api.util;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 05/08/13
 * Time: 17:29
 */
public class ArrayUtil {
	public static int[] Join(int[] first, int... second) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i : first) {
			list.add(i);
		}
		for (int i : second) {
			list.add(i);
		}

		int[] out = new int[list.size()];
		int i = 0;
		for (Integer integer : list) {
			out[i++] = integer;
		}

		return out;
	}
}
