package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;

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
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.Result;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class ImdbSearchController {

	private static final Logger LOGGER = LogManager.getLogger(ImdbSearchController.class);
	
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
	public String imdbSearch(Model model) {
		model.addAttribute("searchForm", new SearchForm());
		return "imdbsearch/imdbSearch";
	}

	@RequestMapping("/imdbsearch/searchSubmit")
	public String imdbSearchSubmit(Model model, Principal principal,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		if (!result.hasErrors()) {
			String title = searchForm.getTitle();
			String year = (StringUtils.isEmptyOrWhitespace(searchForm.getYear()))? null : searchForm.getYear().trim();
			SearchResult searchResult = null;
			try {
				searchResult = imdbSearchService.searchImdb(title, Integer.valueOf(1), year);
			} catch (ServiceLimitExceededException e) {
				ViewUtil.setErrorMessage(model, "The maximum number of allowed IMDB service calls for today has been reached.  Please retry at a later date.");
				return "imdbsearch/imdbSearch";
			}
			List<Result> searchResults = searchResult.getResults();
			model.addAttribute("totalResults", searchResult.getTotalResults());
			model.addAttribute("searchResults", searchResults);
			MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
			if (defaultMovieCollection != null) {
				model.addAttribute("defaultMovieCollection", defaultMovieCollection);
				if (searchResults != null && searchResults.size() > 0) {
					Set<String> imdbIdsInCollection = movieService.getImdbIdsInDefaultCollection(principal.getName());
					for (Result r : searchResults) {
						if (imdbIdsInCollection.contains(r.getImdbID())) {
							r.setInCollection(true);
						}
					}
				}
			}
		}
		return "imdbsearch/imdbSearch";
	}
	
	@RequestMapping(value="/imdbsearch/addToCollection", produces=MediaType.APPLICATION_JSON_VALUE)
	public String addToCollection(Model model, Principal principal, @RequestParam String imdbId) {
		LOGGER.info("Add To Collection called with ID " + imdbId);
		MovieDetails movieDetails = null;
		try {
			movieDetails = imdbSearchService.getMovieDetails(imdbId);
		} catch (ServiceLimitExceededException e1) {
			return ViewUtil.ajaxResponse(model, 
					"Service limit exceeded. You will not be able to add any more movies to your collection today through the IMDB Search function.",
					new String[] {"imdbId"}, new String[] {imdbId});
		}
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = new Movie(movieDetails, movieCollection.getId());
		try {
			movieService.addMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			return ViewUtil.ajaxResponse(model, 
					"You cannot add movies to the collection.",
					new String[] {"imdbId"}, new String[] {imdbId});
		}
		return ViewUtil.ajaxResponse(model, new String[] {"imdbId"}, new String[] {imdbId});
	}
}
