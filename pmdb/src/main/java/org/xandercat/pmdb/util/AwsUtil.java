package org.xandercat.pmdb.util;

public class AwsUtil {

//	private static String encode(String key, String data) throws Exception {
//		Mac mac = Mac.getInstance("HmacSHA256");
//		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
//		mac.init(secretKeySpec);
//		return Hex.encodeHexString(mac.doFinal(data.getBytes("UTF-8")));
//	}

	public static String createV4SigningKey(String secretAccessKey) {
//		String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
//		String encodedKey = encode("AWS4" + secretAccessKey, dateString);
//		encodedKey = encode(encodedKey, Region.US_EAST_1.id());
//		encodedKey = encode(encodedKey, DynamoDbClient.SERVICE_NAME);
//		encodedKey = encode(encodedKey, SignerConstants.AWS4_TERMINATOR);
		return null;
	}
}
