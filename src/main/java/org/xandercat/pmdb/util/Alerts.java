package org.xandercat.pmdb.util;

import java.util.HashSet;
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

	/**
	 * Class to represent one alert message.  Message can be either an actual message or a
	 * message key to a message in a message properties file.
	 * 
	 * @author Scott Arnold
	 */
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
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sessionKey == null) ? 0 : sessionKey.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Alert other = (Alert) obj;
			if (sessionKey == null) {
				if (other.sessionKey != null)
					return false;
			} else if (!sessionKey.equals(other.sessionKey))
				return false;
			return true;
		}
	}
	
	/**
	 * Enum to represent the type of alert.  Classname within this class represents the HTML class name
	 * that should be used for the alert, not the Java class name.
	 * 
	 * @author Scott Arnold
	 */
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
	
	/**
	 * Sets a success message in the model.  Message should be actual message, not a message key.
	 * 
	 * @param model    model
	 * @param message  message
	 */
	public static void setMessage(Model model, String message) {
		setMessage(model, message, false);
	}
	
	/**
	 * Sets a success message in the model.  Message should be a message key, not an actual message.
	 * 
	 * @param model       model
	 * @param messageKey  message key from message properties file
	 */
	public static void setMessageWithKey(Model model, String messageKey) {
		setMessage(model, messageKey, true);
	}
	
	/**
	 * Sets a success message in the model.
	 * 
	 * @param model       model
	 * @param message     message or message key
	 * @param messageKey  whether the message parameter represents a message key rather than an actual message
	 */
	public static void setMessage(Model model, String message, boolean messageKey) {
		model.addAttribute(KEY_MESSAGE, new Alert(KEY_MESSAGE, AlertType.SUCCESS, message, messageKey));
	}
	
	/**
	 * Sets an error message in the model.  Message should be actual message, not a message key.
	 * 
	 * @param model    model
	 * @param message  message
	 */
	public static void setErrorMessage(Model model, String errorMessage) {
		setErrorMessage(model, errorMessage, false);
	}
	
	/**
	 * Sets an error message in the model.  Message should be a message key, not an actual message.
	 * 
	 * @param model       model
	 * @param messageKey  message key from message properties file
	 */
	public static void setErrorMessageWithKey(Model model, String errorMessageKey) {
		setErrorMessage(model, errorMessageKey, true);
	}
	
	/**
	 * Sets an error message in the model.
	 * 
	 * @param model       model
	 * @param message     message or message key
	 * @param messageKey  whether the message parameter represents a message key rather than an actual message
	 */
	public static void setErrorMessage(Model model, String errorMessage, boolean messageKey) {
		model.addAttribute(KEY_ERROR_MESSAGE, new Alert(KEY_ERROR_MESSAGE, AlertType.DANGER, errorMessage, messageKey));
	}

	/**
	 * Sets an error message in the flash attributes.
	 * 
	 * @param redirectAttributes  redirectAttributes
	 * @param message             message or message key
	 * @param messageKey          whether the message parameter represents a message key rather than an actual message
	 */
	public static void setErrorMessage(RedirectAttributes redirectAttributes, String errorMessage, boolean messageKey) {
		redirectAttributes.addFlashAttribute(KEY_ERROR_MESSAGE, new Alert(KEY_ERROR_MESSAGE, AlertType.DANGER, errorMessage, messageKey));
	}

	/**
	 * Sets an alert message tied to the user session (session scope).  Alert will be set both within the session 
	 * and the model for the current request.
	 * 
	 * @param model       model
	 * @param session     session
	 * @param sessionKey  session key for alert
	 * @param type        alert type
	 * @param message     message
	 */
	public static void setSessionAlert(Model model, HttpSession session, String sessionKey, AlertType type, String message) {
		setSessionAlert(model, session, sessionKey, type, message, false);
	}
	
	/**
	 * Sets an alert message tied to the user session (session scope).  Alert will be set both within the session 
	 * and the model for the current request.
	 * 
	 * @param model       model
	 * @param session     session
	 * @param sessionKey  session key for alert
	 * @param type        alert type
	 * @param messageKey  message key from message properties
	 */
	public static void setSessionAlertWithKey(Model model, HttpSession session, String sessionKey, AlertType type, String messageKey) {
		setSessionAlert(model, session, sessionKey, type, messageKey, true);
	}
	
	/**
	 * Sets an alert message tied to the user session (session scope).  Alert will be set both within the session
	 * and the model for the current request.
	 * 
	 * @param model       model
	 * @param session     session
	 * @param key         session key
	 * @param type        alert type
	 * @param message     message or message key
	 * @param messageKey  whether or not the message is a message key
	 */
	@SuppressWarnings("unchecked")
	public static void setSessionAlert(Model model, HttpSession session, String key, AlertType type, String message, boolean messageKey) {
		Set<String> dismissedKeys = (Set<String>) session.getAttribute(KEY_DISMISSED_KEYS);
		if (dismissedKeys != null && dismissedKeys.contains(key)) {
			return;
		}
		Set<Alert> alerts = (Set<Alert>) model.getAttribute(KEY_OTHER_MESSAGES);
		if (alerts == null) {
			alerts = new HashSet<Alert>();
			model.addAttribute(KEY_OTHER_MESSAGES, alerts);
		}
		if (!alerts.stream().anyMatch(alert -> key.equals(alert.getSessionKey()))) {
			alerts.add(new Alert(key, type, message, messageKey));
		}
	}
	
	/**
	 * Dismisses a session alert for the remainder of the session.
	 * 
	 * @param session  session
	 * @param key      session key for alert
	 */
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
