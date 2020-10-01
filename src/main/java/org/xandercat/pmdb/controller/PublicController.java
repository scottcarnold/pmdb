package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.form.movie.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbAttribute;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Controller for public operations on movie collections.  
 * 
 * @author Scott Arnold
 */
@Controller
public class PublicController {

	private static final Logger LOGGER = LogManager.getLogger(PublicController.class);
	
	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private MovieService movieService;
	
	/**
	 * Page for listing movies for a collection for an anonymous public user.
	 * 
	 * @param model         model
	 * @param collectionId  collection ID
	 * 
	 * @return public collection view page
	 */
	@RequestMapping("/public/viewCollection")
	public String publicViewCollection(Model model, 
			@RequestParam("collectionId") String collectionId) {
		preparePublicViewCollection(model, collectionId, null);
		SearchForm searchForm = new SearchForm();
		searchForm.setCollectionId(collectionId);
		model.addAttribute("searchForm", searchForm);
		return "public/movies";
	}
	
	/**
	 * Page for searching list of movies for a collection by an anonymous public user.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param searchForm search form
	 * @param result     binding result
	 * 
	 * @return public collection view page
	 */
	@RequestMapping("/public/viewCollectionSearch")
	public String publicViewCollectionSearch(Model model, Principal principal, 
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		preparePublicViewCollection(model, searchForm.getCollectionId(), searchForm.getSearchString());
		return "public/movies";
	}
	
	private void preparePublicViewCollection(Model model, String collectionId, String searchString) {
		try {
			MovieCollection movieCollection = collectionService.getPublicMovieCollection(collectionId);
			model.addAttribute("defaultMovieCollection", movieCollection);
			Set<Movie> movies = movieService.getMoviesForCollection(movieCollection.getId(), movieCollection.getOwner());
			model.addAttribute("totalMoviesInCollection", movies.size());
			model.addAttribute("unlinkCount", movies.stream()
					.filter(movie -> FormatUtil.isBlank(movie.getAttribute(ImdbAttribute.IMDB_ID.getKey())))
					.count());
			if (FormatUtil.isNotBlank(searchString)) {
				movies = movieService.searchMoviesForCollection(movieCollection.getId(), searchString, movieCollection.getOwner());
			}
			List<String> attrColumns = movieService.getTableColumnPreferences(movieCollection.getOwner());
			model.addAttribute("movies", movies);
			model.addAttribute("attrFormatters", ViewUtil.getDataFormatters(movies, attrColumns));
			model.addAttribute("attrColumns", attrColumns);
			model.addAttribute("attributeNames", getAttributeNames(movieCollection.getOwner()));
			
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to retrieve movies for public movie collection request.", e);
			Alerts.setErrorMessage(model, "This movie collection is not available.");
		}		
	}

	public List<String> getAttributeNames(String userName) {
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(userName);
		if (movieCollection.isPresent()) {
			try {
				return movieService.getAttributeKeysForCollection(movieCollection.get().getId(), userName);
			} catch (Exception e) {
				LOGGER.error("Unable to add collection attribute names to model.", e);
			}			
		}
		return new ArrayList<String>();
	}
	
	/**
	 * AJAX method for returning the full details of a single movie in the movie collection.  Returned result is 
	 * an HTML fragment that can be loaded into a dialog.
	 * 
	 * @param model      model
	 * @param movieId    id of movie to get details for
	 * 
	 * @return page fragment for movie details
	 */
	@RequestMapping("/public/movieDetails")
	public String movieDetails(Model model, @RequestParam String movieId) {
		try {
			Optional<Movie> movie = movieService.getPublicMovie(movieId);
			if (movie.isPresent()) {
				model.addAttribute("movie", movie.get());
				MovieCollection movieCollection = collectionService.getPublicMovieCollection(movie.get().getCollectionId());
				model.addAttribute("defaultMovieCollection", movieCollection);
			}
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to load movie id " + movieId + " for public user", e);
		}
		return "public/movieDetails";
	}
}
