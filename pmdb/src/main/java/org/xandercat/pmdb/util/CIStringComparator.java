package org.xandercat.pmdb.util;

import java.util.Comparator;

public class CIStringComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1 == null) {
			return -1;
		}
		return o1.compareToIgnoreCase(o2);
	}
}
