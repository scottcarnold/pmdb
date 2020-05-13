package org.xandercat.pmdb.util;

import org.springframework.ui.Model;

public class ViewUtil {

	public static String TAB_HOME = "isHomeTab";
	public static String TAB_COLLECTIONS = "isCollectionsTab";
	public static String TAB_IMDB_SEARCH = "isImdbSearchTab";
	public static String TAB_USER_ADMIN = "isUserAdminTab";
	public static String TAB_MY_ACCOUNT = "isMyAccountTab";
	
	public static void setErrorMessage(Model model, String errorMessage) {
		model.addAttribute("alertErrorMessage", errorMessage);
	}
	
	public static void setMessage(Model model, String message) {
		model.addAttribute("alertMessage", message);
	}
}
