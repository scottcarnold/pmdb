package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
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
import org.xandercat.pmdb.dto.imdb.MovieDetailsRequest;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.Result;
import org.xandercat.pmdb.dto.imdb.SearchRequest;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.ajax.JsonResponse;

/**
 * Controller for functions involving IMDB search.  This includes IMDB browsing, adding new
 * movies using IMDB as the information source, and associating existing movie collection 
 * movies with movies on the IMDB.
 * 
 * @author Scott Arnold
 */
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

	/**
	 * Handle linking operation on movie, returning final view to navigate to.
	 * 
	 * @param model       model
	 * @param principal   principal
	 * @param searchForm  search form
	 * @param result      binding result
	 * @param session     http session
	 * @param movie       movie being linked
	 * @return view to navigate to
	 * @throws ServiceLimitExceededException 
	 * @throws WebServicesException 
	 * @throws CollectionSharingException 
	 */
	private String handleLinkImdb(Model model, Principal principal, SearchForm searchForm, BindingResult result, HttpSession session, Movie movie) 
			throws WebServicesException, ServiceLimitExceededException, CollectionSharingException {
		String linkId = searchForm.getLinkImdbId().trim();
		if ("unlink".equals(linkId)) {
			imdbSearchService.removeImdbAttributes(movie);
			movieService.updateMovie(movie, principal.getName());	
		} else if (!"skip".equals(linkId)) {
			MovieDetails movieDetails = imdbSearchService.getMovieDetails(new MovieDetailsRequest(searchForm.getLinkImdbId().trim()));
			imdbSearchService.addImdbAttributes(movie, movieDetails);
			movieService.updateMovie(movie, principal.getName());
		}
		if (searchForm.isLinkAll()) {
			final String previousTitle = movie.getTitle().toLowerCase();
			List<Movie> unlinkedMovies = movieService.getUnlinkedMoviesForDefaultCollection(principal.getName());
			if (unlinkedMovies.size() > 0) {
				Optional<Movie> nextMovie = unlinkedMovies.stream()
						.filter(uMovie -> uMovie.getTitle().toLowerCase().compareTo(previousTitle) > 0)
						.findFirst();
				if (!nextMovie.isPresent()) {
					// wrap back around to beginning of list
					nextMovie = unlinkedMovies.stream().findFirst();
				}
				searchForm.setLinkMovieId(nextMovie.get().getId());
				searchForm.setLinkImdbId(null); // this is critical or it would trigger infinite recursion
				searchForm.setTitle(nextMovie.get().getTitle());
				searchForm.setPage(1);
				return imdbSearchSubmit(model, principal, searchForm, result, session);
			}
		}
		return "redirect:/";		
	}
	
	/**
	 * Handles link mode operations, storing movie to link in the model, and either returning final view to navigate to, 
	 * or empty if no actual link needs to be performed.
	 * 
	 * @param model       model
	 * @param principal   principal
	 * @param searchForm  search form
	 * @param result      binding result
	 * @param session     http session
	 * @return view to navigate to, or empty if unable to successfully handle linking
	 */
	private Optional<String> handleLinkMovie(Model model, Principal principal, SearchForm searchForm, BindingResult result, HttpSession session) {
		try {
			Optional<Movie> movie = movieService.getMovie(searchForm.getLinkMovieId(), principal.getName());
			collectionService.assertCollectionEditable(movie.get().getCollectionId(), principal.getName());
			if (!StringUtils.isEmptyOrWhitespace(searchForm.getLinkImdbId())) {
				// link movie and return to movie list or go to next unlinked movie
				return Optional.of(handleLinkImdb(model, principal, searchForm, result, session, movie.get()));
			}
			model.addAttribute("linkMovie", movie.get());
		} catch (Exception e) {
			LOGGER.error("Unable to get movie to link.", e);
			Alerts.setErrorMessage(model, "This movie cannot be linked.");
		}
		return Optional.empty();
	}
	
	@RequestMapping("/imdbsearch/searchSubmit")
	public String imdbSearchSubmit(Model model, Principal principal,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result, HttpSession session) {
		
		// handle alerts and errors
		Alerts.setSessionAlertWithKey(model, session, "IMDBSearchLimit", Alerts.AlertType.WARNING, "alert.imdbsearch.limits");
		if (result.hasErrors()) {
			return "imdbsearch/imdbSearch";
		}
		
		// handle mode where user is linking movies in their collection to IMDB
		if (!StringUtils.isEmptyOrWhitespace(searchForm.getLinkMovieId())) {
			Optional<String> returnView = handleLinkMovie(model, principal, searchForm, result, session);
			if (returnView.isPresent()) {
				return returnView.get();
			}
		}
		
		// execute search
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
			searchResult = imdbSearchService.searchImdb(new SearchRequest(title, year, Integer.valueOf(searchForm.getPage())));
			session.setAttribute(SESSION_KEY_LAST_SEARCH, currentHash);
			model.addAttribute("searched", Boolean.TRUE);
		} catch (ServiceLimitExceededException e) {
			Alerts.setErrorMessage(model, "The maximum number of allowed IMDB service calls for today has been reached.  Please retry at a later date.");
			return "imdbsearch/imdbSearch";
		} catch (WebServicesException wse) {
			// with a little more work we could still provide paginator, as sometimes only a specific page fails. (example: "dragon" page 3 results in error 5/23/2020)
			Alerts.setErrorMessage(model, "Search results could not be obtained for this page.");
			return "imdbsearch/imdbSearch";
		}
		List<Result> searchResults = searchResult.getResults();
		if (StringUtils.isEmptyOrWhitespace(searchResult.getTotalResults())) {
			model.addAttribute("totalResults", Integer.valueOf(0));
		} else {
			model.addAttribute("totalResults", Integer.valueOf(searchResult.getTotalResults()));
		}
		model.addAttribute("searchResults", searchResults);
		
		// pass information about users default collection to view
		Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (defaultMovieCollection.isPresent()) {
			model.addAttribute("defaultMovieCollection", defaultMovieCollection.get());
			if (searchResults != null && searchResults.size() > 0) {
				try {
					Set<String> imdbIdsInCollection = movieService.getImdbIdsInDefaultCollection(principal.getName());
					searchResults.stream()
							.filter(r -> imdbIdsInCollection.contains(r.getImdbID()))
							.forEach(r -> r.setInCollection(true));
				} catch (WebServicesException e) {
					LOGGER.error("Unable to read IMDB IDs from collection.", e);
					Alerts.setErrorMessage(model, "What movies you already have in your collection will not be indicated due to an error accessing your movie collection.");
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
			movieDetails = imdbSearchService.getMovieDetails(new MovieDetailsRequest(imdbId));
		} catch (ServiceLimitExceededException e1) {
			response.setOk(false);
			response.setErrorMessage("Service limit exceeded. You will not be able to add any more movies to your collection today through the IMDB Search function.");
			return response;
		} catch (WebServicesException wse) {
			response.setOk(false);
			response.setErrorMessage("Unable to retrieve details for this movie from the IMDB.  Movie could not be added to your collection.");
			return response;
		}
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = new Movie();
		movie.setCollectionId(movieCollection.get().getId());
		imdbSearchService.addImdbAttributes(movie, movieDetails);
		try {
			movieService.addMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			response.setOk(false);
			response.setErrorMessage("You cannot add movies to the collection.");
			return response;
		} catch (WebServicesException e) {
			LOGGER.error("Unable to add IMDB movie to collection.", e);
			response.setOk(false);
			response.setErrorMessage("Unable to add movie to the collection due to a problem with cloud services.");
			return response;			
		}
		return response;
	}
	
	@RequestMapping("/imdbsearch/link")
	public String link(Model model, Principal principal, @RequestParam String movieId, @RequestParam boolean linkAll, HttpSession session) {
		try {
			if ("any".equals(movieId)) {
				Optional<Movie> anyMovie = movieService.getUnlinkedMoviesForDefaultCollection(principal.getName()).stream().findFirst();
				if (anyMovie.isPresent()) {
					movieId = anyMovie.get().getId();
				}
			}
			Optional<Movie> movie = movieService.getMovie(movieId, principal.getName());
			if (!movie.isPresent()) {
				Alerts.setErrorMessage(model, "Requested movie not found.");
				return imdbSearch(model, session);
			}
			collectionService.assertCollectionEditable(movie.get().getCollectionId(), principal.getName());
			SearchForm searchForm = new SearchForm();
			searchForm.setTitle(movie.get().getTitle());
			searchForm.setLinkMovieId(movie.get().getId());
			searchForm.setLinkAll(linkAll);
			model.addAttribute("searchForm", searchForm);
			return imdbSearchSubmit(model, principal, searchForm, ViewUtil.emptyBindingResult("searchForm"), session);
		} catch (Exception e) {
			LOGGER.error("Unable to access movie collection.", e);
			Alerts.setErrorMessage(model, "You cannot add movies to the collection.");
			return imdbSearch(model, session);
		} 
		
		
	}
}
