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
		private String message;     // a basic string message
		private boolean messageKey; // if true, indicates message is a resource message key
		private String sessionKey;  // key for session tracking
		private AlertType type;
		public Alert(String sessionKey, AlertType type, String message, boolean messageKey) {
			this.sessionKey = sessionKey;
			this.type = type;
			this.message = message;
			this.messageKey = messageKey;
		}
		public String getMessage() {
			return message;
		}
		public String getSessionKey() {
			return sessionKey;
		}
		public AlertType getType() {
			return type;
		}
		public boolean isMessageKey() {
			return messageKey;
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
		setMessage(model, message, false);
	}
	
	public static void setMessageWithKey(Model model, String messageKey) {
		setMessage(model, messageKey, true);
	}
	
	public static void setMessage(Model model, String message, boolean messageKey) {
		model.addAttribute(KEY_MESSAGE, new Alert(KEY_MESSAGE, AlertType.SUCCESS, message, messageKey));
	}
	
	public static void setErrorMessage(Model model, String errorMessage) {
		setErrorMessage(model, errorMessage, false);
	}
	
	public static void setErrorMessageWithKey(Model model, String errorMessageKey) {
		setErrorMessage(model, errorMessageKey, true);
	}
	
	public static void setErrorMessage(Model model, String errorMessage, boolean messageKey) {
		model.addAttribute(KEY_ERROR_MESSAGE, new Alert(KEY_ERROR_MESSAGE, AlertType.DANGER, errorMessage, messageKey));
	}
	
	public static void setErrorMessage(RedirectAttributes redirectAttributes, String errorMessage, boolean messageKey) {
		redirectAttributes.addFlashAttribute(KEY_ERROR_MESSAGE, new Alert(KEY_ERROR_MESSAGE, AlertType.DANGER, errorMessage, messageKey));
	}

	public static void setSessionAlertWithKey(Model model, HttpSession session, String sessionKey, AlertType type, String messageKey) {
		setSessionAlert(model, session, sessionKey, type, messageKey, true);
	}
	
	public static void setSessionAlert(Model model, HttpSession session, String sessionKey, AlertType type, String message) {
		setSessionAlert(model, session, sessionKey, type, message, false);
	}
	
	@SuppressWarnings("unchecked")
	public static void setSessionAlert(Model model, HttpSession session, String key, AlertType type, String message, boolean messageKey) {
		Set<String> dismissedKeys = (Set<String>) session.getAttribute(KEY_DISMISSED_KEYS);
		if (dismissedKeys != null && dismissedKeys.contains(key)) {
			return;
		}
		List<Alert> alerts = (List<Alert>) model.getAttribute(KEY_OTHER_MESSAGES);
		if (alerts == null) {
			alerts = new ArrayList<Alert>();
			model.addAttribute(KEY_OTHER_MESSAGES, alerts);
		}
		alerts.add(new Alert(key, type, message, messageKey));
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
