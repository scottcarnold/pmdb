package org.xandercat.pmdb.util;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.xandercat.pmdb.service.CollectionService;

public class ViewUtil {

	public static String TAB_HOME = "isHomeTab";
	public static String TAB_COLLECTIONS = "isCollectionsTab";
	public static String TAB_IMDB_SEARCH = "isImdbSearchTab";
	public static String TAB_USER_ADMIN = "isUserAdminTab";
	public static String TAB_MY_ACCOUNT = "isMyAccountTab";
	
	public static String SESSION_NUM_SHARE_OFFERS_KEY = "numShareOffers";
	
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
}
