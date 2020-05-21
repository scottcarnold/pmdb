package org.xandercat.pmdb.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xandercat.pmdb.util.Alerts;

@ControllerAdvice
public class MultipartExceptionHandler {

	private static final Logger LOGGER = LogManager.getLogger(MultipartExceptionHandler.class);
	
	@ExceptionHandler(MultipartException.class)
	public String handleError(MultipartException e, RedirectAttributes redirectAttributes) {
		LOGGER.error("Error during file upload.", e);
		Alerts.setErrorMessage(redirectAttributes, "An error occurred while uploading your file.");
		return "redirect:/collections/import"; // only one place this can happen, during collections upload; if there were more places, would have to rethink this
	}

}
