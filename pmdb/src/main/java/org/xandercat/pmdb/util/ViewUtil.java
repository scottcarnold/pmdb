package org.xandercat.pmdb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.xandercat.pmdb.form.Option;
import org.xandercat.pmdb.service.CollectionService;

public class ViewUtil {

	public static String TAB_HOME = "isHomeTab";
	public static String TAB_COLLECTIONS = "isCollectionsTab";
	public static String TAB_IMDB_SEARCH = "isImdbSearchTab";
	public static String TAB_USER_ADMIN = "isUserAdminTab";
	public static String TAB_MY_ACCOUNT = "isMyAccountTab";
	
	public static String SESSION_NUM_SHARE_OFFERS_KEY = "numShareOffers";
	public static String SESSION_MOVIES_EDIT_MODE_KEY = "moviesEditMode";
	
	public static void setErrorMessage(Model model, String errorMessage) {
		model.addAttribute("alertErrorMessage", errorMessage);
	}
	
	public static void setMessage(Model model, String message) {
		model.addAttribute("alertMessage", message);
	}
	
	public static void updateNumShareOffers(CollectionService collectionService, HttpSession session, String username) {
		int numShareOffers = collectionService.getShareOfferMovieCollections(username).size();
		if (numShareOffers > 0) {
			session.setAttribute(SESSION_NUM_SHARE_OFFERS_KEY, Integer.valueOf(numShareOffers));
		} else {
			session.removeAttribute(SESSION_NUM_SHARE_OFFERS_KEY);
		}
	}
	
	public static boolean isMoviesEditMode(HttpSession session) {
		return Boolean.TRUE.equals(session.getAttribute(SESSION_MOVIES_EDIT_MODE_KEY));
	}
	
	public static void setMoviesEditMode(HttpSession session, boolean editMode) {
		if (editMode) {
			session.setAttribute(SESSION_MOVIES_EDIT_MODE_KEY, Boolean.TRUE);
		} else {
			session.removeAttribute(SESSION_MOVIES_EDIT_MODE_KEY);
		}
	}
	
	/**
	 * Shortcut method to return list of options for view given collection of objects.  Value field is currently
	 * limited to primitive int type.  Text field is currently limited to String type.
	 * 
	 * @param items            items to create list of options for
	 * @param valueFieldName   field name for the value (int type)
	 * @param textFieldName    field name for the text (String type)
	 * 
	 * @return list of options for the items
	 */
	public static List<Option> getOptions(Collection<?> items, String valueFieldName, String textFieldName) {
		List<Option> options = new ArrayList<Option>();
		try {
			for (Object item : items) {
				String value = String.valueOf(ReflectionUtil.invokeGetter(item, valueFieldName, Integer.TYPE));
				String text = ReflectionUtil.invokeGetter(item, textFieldName, String.class);
				options.add(new Option(value, text));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to generate list of options from provided items.", e);
		}
		return options;
	}
	
	/**
	 * Shortcut method to return list of options for the given enum class type.  Enum name will be used for 
	 * value, and toString() will be used for text.
	 * 
	 * @param enumType  enum class
	 * @return          list of options for enum class
	 */
	public static <E extends Enum<E>> List<Option> getOptions(Class<E> enumType) {
		List<Option> options = new ArrayList<Option>();
		for (Enum<?> e : enumType.getEnumConstants()) {
			options.add(new Option(e.name(), e.toString()));
		}
		return options;
	}
}
