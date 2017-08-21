package org.g4studio.system.common.util.idgenerator;

import java.util.UUID;

/**
 * provides generator of UUID.
 * by zhangweibq
 * 
 */
public class UUIDGenerator {
	
	public static String nextIdentifier() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
