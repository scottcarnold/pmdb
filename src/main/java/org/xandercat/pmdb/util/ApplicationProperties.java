package org.xandercat.pmdb.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for generic application properties and feature flags.
 * 
 * @author Scott Arnold
 */
@Component
public class ApplicationProperties {

	@Value("${aws.enable:false}")
	private boolean awsEnabled;

	public boolean isAwsEnabled() {
		return awsEnabled;
	}

	public void setAwsEnabled(boolean awsEnabled) {
		this.awsEnabled = awsEnabled;
	}

}
