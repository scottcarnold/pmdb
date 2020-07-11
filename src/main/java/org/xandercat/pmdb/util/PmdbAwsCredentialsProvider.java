package org.xandercat.pmdb.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * Class for providing AWS Cloud credentials.
 * 
 * @author Scott Arnold
 */
public class PmdbAwsCredentialsProvider implements AWSCredentialsProvider {
	
	public static class PmdbAwsCredentials implements AWSCredentials {
		
		private String accessKeyId;
		private String secretAccessKey;
		
		public PmdbAwsCredentials(String accessKeyId, String secretAccessKey) {
			this.accessKeyId = accessKeyId;
			this.secretAccessKey = secretAccessKey;
		}

		@Override
		public String getAWSAccessKeyId() {
			return accessKeyId;
		}

		@Override
		public String getAWSSecretKey() {
			return secretAccessKey;
		}	
	}
	
	private AWSCredentials awsCredentials;
	
	public PmdbAwsCredentialsProvider(String accessKeyId, String secretAccessKey) throws Exception {
		this.awsCredentials = new PmdbAwsCredentials(accessKeyId, secretAccessKey);
	}
	
	@Override
	public AWSCredentials getCredentials() {
		return awsCredentials;
	}

	@Override
	public void refresh() {
		// noop
	}
}
