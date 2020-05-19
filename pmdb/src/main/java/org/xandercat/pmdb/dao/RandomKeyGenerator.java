package org.xandercat.pmdb.dao;

import java.util.UUID;

public class RandomKeyGenerator implements KeyGenerator {

	@Override
	public String getKey() {
		return UUID.randomUUID().toString();
	}
}
