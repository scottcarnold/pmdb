package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbRestService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class ImdbSearchController {

	private static final Logger LOGGER = LogManager.getLogger(ImdbSearchController.class);
	
	@Autowired
	private ImdbRestService imdbRestService;
	
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
			SearchResult searchResult = imdbRestService.searchImdb(title, Integer.valueOf(1), year);
			List<Result> searchResults = searchResult.getResults();
			model.addAttribute("totalResults", searchResult.getTotalResults());
			model.addAttribute("searchResults", searchResults);
		}
		return "imdbsearch/imdbSearch";
	}
	
	@RequestMapping("/imdbsearch/addToCollection")
	public String addToCollection(Model model, Principal principal, @RequestParam String imdbId) {
		MovieDetails movieDetails = imdbRestService.getMovieDetails(imdbId);
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = new Movie(movieDetails, movieCollection.getId());
		try {
			movieService.addMovie(movie, principal.getName());
			ViewUtil.setMessage(model, "Movie added to active movie collection.");
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to add IMDB movie to collection.");
		}
		return imdbSearch(model);
	}
}
