package org.xandercat.pmdb.dao;

import java.util.UUID;

/**
 * KeyGenerator class for generating random keys utilizing UUID.
 * 
 * @author Scott Arnold
 */
public class RandomKeyGenerator implements KeyGenerator {

	@Override
	public String getKey() {
		return UUID.randomUUID().toString();
	}
}
