package org.xandercat.pmdb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;
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
	public static String SESSION_COLLECTION_UPLOAD_FILE = "importedCollectionFile";
	public static String SESSION_COLLECTION_UPLOAD_SHEETS = "importedCollectionSheetNames";
	public static String SESSION_COLLECTION_UPLOAD_COLUMNS = "importedCollectionColumnNames";
	
	public static void setErrorMessage(Model model, String errorMessage) {
		model.addAttribute("alertErrorMessage", errorMessage);
	}
	
	public static void setErrorMessage(RedirectAttributes redirectAttributes, String errorMessage) {
		redirectAttributes.addFlashAttribute("alertErrorMessage", errorMessage);
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
	
	public static MultipartFile getImportedCollectionFile(HttpSession session) {
		return (MultipartFile) session.getAttribute(SESSION_COLLECTION_UPLOAD_FILE);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getImportedCollectionSheets(HttpSession session) {
		return (List<String>) session.getAttribute(SESSION_COLLECTION_UPLOAD_SHEETS);
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getImportedCollectionColumns(HttpSession session) {
		return (List<String>) session.getAttribute(SESSION_COLLECTION_UPLOAD_COLUMNS);
	}
	public static void setImportedCollectionFile(HttpSession session, MultipartFile multipartFile, List<String> sheets, List<String> columns) {
		session.setAttribute(SESSION_COLLECTION_UPLOAD_FILE, multipartFile);
		session.setAttribute(SESSION_COLLECTION_UPLOAD_SHEETS, sheets);
		session.setAttribute(SESSION_COLLECTION_UPLOAD_COLUMNS, columns);
	}
	
	public static void clearImportedCollectionFile(HttpSession session) {
		session.removeAttribute(SESSION_COLLECTION_UPLOAD_FILE);
		session.removeAttribute(SESSION_COLLECTION_UPLOAD_SHEETS);
		session.removeAttribute(SESSION_COLLECTION_UPLOAD_COLUMNS);
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
	
	/**
	 * Shortcut method to return list of options for the given collection of strings.  Each String 
	 * will be used for both the value and text of it's returned option.
	 * 
	 * @param strings strings to create options for
	 * @return options for the strings
	 */
	public static List<Option> getOptions(Collection<String> strings) {
		List<Option> options = new ArrayList<Option>();
		for (String s : strings) {
			options.add(new Option(s, s));
		}
		return options;
	}
	
	public static String ajaxResponse(Model model, String errorMessage, String[] keys, String[] values) {
		Map<String, String> responseValues = new HashMap<String, String>();
		for (int i=0; i<keys.length; i++) {
			responseValues.put(keys[i], values[i]);
		}
		return ajaxResponse(model, errorMessage, responseValues);
	}
	
	//TODO: Replace the raw construction of a JSON response with a JSON framework generated response
	public static String ajaxResponse(Model model, String errorMessage, Map<String, String> responseValues) {
		if (StringUtils.isEmptyOrWhitespace(errorMessage)) {
			model.addAttribute("isOk", "true");
			model.addAttribute("errorMessage", errorMessage);
		} else {
			model.addAttribute("isOk", "false");
			model.addAttribute("errorMessage", "");
		}		
		if (responseValues == null || responseValues.size() == 0) {
			model.addAttribute("content", "\"\"");
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean first = true;
			for (Map.Entry<String, String> entry : responseValues.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				//TODO: both key and value need to be escaped
				sb.append("\"").append(entry.getKey()).append("\" : \"").append(entry.getValue()).append("\"");
			}
			sb.append("}");
			model.addAttribute("content", sb.toString());
		}
		return "ajax/response";
	}
	
	public static String ajaxResponse(Model model, Map<String, String> responseValues) {
		return ajaxResponse(model, null, responseValues);
	}
	
	public static String ajaxResponse(Model model, String[] keys, String[] values) {
		return ajaxResponse(model, null, keys, values);
	}
}
