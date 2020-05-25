package org.xandercat.pmdb.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.multipart.MultipartFile;
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
	
	private static Option reflectOptionWithIntValue(Object item, String valueGetterName, String textGetterName) {
		try {
			Method valueMethod = ReflectionUtils.findRequiredMethod(item.getClass(), valueGetterName, (Class<?>[]) null);
			Method textMethod = ReflectionUtils.findRequiredMethod(item.getClass(), textGetterName, (Class<?>[]) null);
			Object value = valueMethod.invoke(item, (Object[]) null);
			Object text = textMethod.invoke(item, (Object[]) null);
			return new Option(value, text);	
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to obtain option text and value with reflection.");
		}
	}
	
	private static <E extends Enum<E>> Option getOption(E e) {
		return new Option(e.name(), e.toString());
	}
	
	private static Option getOption(String s) {
		return new Option(s, s);
	}
	
	/**
	 * Shortcut method to return list of options for view given collection of objects.  Value field is currently
	 * limited to primitive int type.  Text field is currently limited to String type.
	 * 
	 * @param items            items to create list of options for
	 * @param valueGetterName   getter method name for the value (int type)
	 * @param textGetterName    getter method name for the text (String type)
	 * 
	 * @return list of options for the items
	 */
	public static List<Option> getOptions(Collection<?> items, String valueGetterName, String textGetterName) {
		return items.stream().map(item -> { 
			return reflectOptionWithIntValue(item, valueGetterName, textGetterName); 
		}).collect(Collectors.toList());
	}
	
	/**
	 * Shortcut method to return list of options for the given enum class type.  Enum name will be used for 
	 * value, and toString() will be used for text.
	 * 
	 * @param enumType  enum class
	 * @return          list of options for enum class
	 */
	public static <E extends Enum<E>> List<Option> getOptions(Class<E> enumType) {
		return Arrays.stream(enumType.getEnumConstants()).map(ViewUtil::getOption).collect(Collectors.toList());
	}
	
	/**
	 * Shortcut method to return list of options for the given collection of strings.  Each String 
	 * will be used for both the value and text of it's returned option.
	 * 
	 * @param strings strings to create options for
	 * @return options for the strings
	 */
	public static List<Option> getOptions(Collection<String> strings) {
		return strings.stream().map(ViewUtil::getOption).collect(Collectors.toList());
	}
	
	public static BindingResult emptyBindingResult(String objectName) {
		return new MapBindingResult(new HashMap<String, String>(), objectName);
	}
}
