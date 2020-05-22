package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.Result;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.CloudServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.ajax.JsonResponse;

@Controller
public class ImdbSearchController {

	private static final Logger LOGGER = LogManager.getLogger(ImdbSearchController.class);
	private static final String SESSION_KEY_LAST_SEARCH = "imdbLastSearchString";
	
	@Autowired
	private ImdbSearchService imdbSearchService;
	
	@Autowired
	private MovieService movieService;
	
	@Autowired
	private CollectionService collectionService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_IMDB_SEARCH;
	}
	
	@RequestMapping("/imdbsearch")
	public String imdbSearch(Model model, HttpSession session) {
		model.addAttribute("searchForm", new SearchForm());
		Alerts.setSessionAlertWithKey(model, session, "IMDBSearchLimit", Alerts.AlertType.WARNING, "alert.imdbsearch.limits");
		return "imdbsearch/imdbSearch";
	}

	@RequestMapping("/imdbsearch/searchSubmit")
	public String imdbSearchSubmit(Model model, Principal principal,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result, HttpSession session) {
		Alerts.setSessionAlertWithKey(model, session, "IMDBSearchLimit", Alerts.AlertType.WARNING, "alert.imdbsearch.limits");
		if (!result.hasErrors()) {
			String title = searchForm.getTitle();
			String year = (StringUtils.isEmptyOrWhitespace(searchForm.getYear()))? null : searchForm.getYear().trim();
			SearchResult searchResult = null;
			try {
				Integer previousHash = (Integer) session.getAttribute(SESSION_KEY_LAST_SEARCH);
				Integer currentHash = Integer.valueOf(searchForm.hashCode());
				if (!currentHash.equals(previousHash)) {
					// search criteria has changed; reset to page 1
					searchForm.setPage(1);
				}
				searchResult = imdbSearchService.searchImdb(title, Integer.valueOf(searchForm.getPage()), year);
				session.setAttribute(SESSION_KEY_LAST_SEARCH, currentHash);
				model.addAttribute("searched", Boolean.TRUE);
			} catch (ServiceLimitExceededException e) {
				Alerts.setErrorMessage(model, "The maximum number of allowed IMDB service calls for today has been reached.  Please retry at a later date.");
				return "imdbsearch/imdbSearch";
			}
			List<Result> searchResults = searchResult.getResults();
			if (StringUtils.isEmptyOrWhitespace(searchResult.getTotalResults())) {
				model.addAttribute("totalResults", Integer.valueOf(0));
			} else {
				model.addAttribute("totalResults", Integer.valueOf(searchResult.getTotalResults()));
			}
			model.addAttribute("searchResults", searchResults);
			MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
			if (defaultMovieCollection != null) {
				model.addAttribute("defaultMovieCollection", defaultMovieCollection);
				if (searchResults != null && searchResults.size() > 0) {
					try {
						Set<String> imdbIdsInCollection = movieService.getImdbIdsInDefaultCollection(principal.getName());
						for (Result r : searchResults) {
							if (imdbIdsInCollection.contains(r.getImdbID())) {
								r.setInCollection(true);
							}
						}
					} catch (CloudServicesException e) {
						LOGGER.error("Unable to read IMDB IDs from collection.", e);
						Alerts.setErrorMessage(model, "What movies you already have in your collection will not be indicated due to an error accessing your movie collection.");
					}
				}
			}
		}
		return "imdbsearch/imdbSearch";
	}
	
	@RequestMapping(value="/imdbsearch/addToCollection", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonResponse addToCollection(Model model, Principal principal, @RequestParam String imdbId) {
		LOGGER.debug("Add To Collection called with ID " + imdbId);
		JsonResponse response = new JsonResponse();
		response.put("imdbId", imdbId);
		MovieDetails movieDetails = null;
		try {
			movieDetails = imdbSearchService.getMovieDetails(imdbId);
		} catch (ServiceLimitExceededException e1) {
			response.setOk(false);
			response.setErrorMessage("Service limit exceeded. You will not be able to add any more movies to your collection today through the IMDB Search function.");
			return response;
		}
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = new Movie(movieDetails, movieCollection.getId());
		try {
			movieService.addMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			response.setOk(false);
			response.setErrorMessage("You cannot add movies to the collection.");
			return response;
		} catch (CloudServicesException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			response.setOk(false);
			response.setErrorMessage("Unable to add movie to the collection due to a problem with cloud services.");
			return response;			
		}
		return response;
	}
}
