package org.xandercat.pmdb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Utility class for managing application alert messages to users. Should be paired with a layout template
 * to present and layout the alerts in the view.
 * 
 * @author Scott Arnold
 */
public class Alerts {
	
	public static final String KEY_MESSAGE = "alertMessage";
	public static final String KEY_ERROR_MESSAGE = "alertErrorMessage";
	public static final String KEY_OTHER_MESSAGES = "alertOtherMessages";
	public static final String KEY_DISMISSED_KEYS = "alertOtherMessagesDismissed";

	public static class Alert {
		private String message;
		private String key;
		private AlertType type;
		public Alert(String key, AlertType type, String message) {
			this.key = key;
			this.type = type;
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
		public String getKey() {
			return key;
		}
		public AlertType getType() {
			return type;
		}
	}
	
	public static enum AlertType {
		SUCCESS("alert-success"), INFO("alert-info"), WARNING("alert-warning"), DANGER("alert-danger");
		private String classname;
		private AlertType(String classname) {
			this.classname = classname;
		}
		public String getClassname() {
			return classname;
		}
	}
	
	public static void setMessage(Model model, String message) {
		model.addAttribute(KEY_MESSAGE, message);
	}
	
	public static void setErrorMessage(Model model, String errorMessage) {
		model.addAttribute(KEY_ERROR_MESSAGE, errorMessage);
	}
	
	public static void setErrorMessage(RedirectAttributes redirectAttributes, String errorMessage) {
		redirectAttributes.addFlashAttribute(KEY_ERROR_MESSAGE, errorMessage);
	}

	@SuppressWarnings("unchecked")
	public static void setSessionAlert(Model model, HttpSession session, String key, AlertType type, String message) {
		Set<String> dismissedKeys = (Set<String>) session.getAttribute(KEY_DISMISSED_KEYS);
		if (dismissedKeys != null && dismissedKeys.contains(key)) {
			return;
		}
		List<Alert> alerts = (List<Alert>) model.getAttribute(KEY_OTHER_MESSAGES);
		if (alerts == null) {
			alerts = new ArrayList<Alert>();
			model.addAttribute(KEY_OTHER_MESSAGES, alerts);
		}
		alerts.add(new Alert(key, type, message));
	}
	
	@SuppressWarnings("unchecked")
	public static void dismissSessionAlert(HttpSession session, String key) {
		Set<String> dismissedKeys = (Set<String>) session.getAttribute(KEY_DISMISSED_KEYS);
		if (dismissedKeys == null) {
			dismissedKeys = new HashSet<String>();
			session.setAttribute(KEY_DISMISSED_KEYS, dismissedKeys);
		}
		dismissedKeys.add(key);
	}
}
