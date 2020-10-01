package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.MovieStatistics;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.form.movie.MovieForm;
import org.xandercat.pmdb.form.movie.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbAttribute;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.DoubleStatistics;
import org.xandercat.pmdb.util.LongStatistics;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.WordStatistics;
import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Controller for movie functions on the user's active movie collection.  Primarily includes
 * searching, adding, editing, and deleting movies.
 * 
 * @author Scott Arnold
 */
@Controller
public class HomeController {

	private static final Logger LOGGER = LogManager.getLogger(HomeController.class);
	
	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private MovieService movieService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_HOME;
	}
	
	@ModelAttribute("attributeNames")
	public List<String> getAttributeNames(Principal principal) {
		Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (defaultMovieCollection.isPresent()) {
			try {
				return movieService.getAttributeKeysForCollection(defaultMovieCollection.get().getId(), principal.getName());
			} catch (Exception e) {
				LOGGER.error("Unable to add collection attribute names to model.", e);
			}			
		}
		return new ArrayList<String>();
	}
	
	/**
	 * Page for listing movies within user's default/active movie collection.  This is considered the main "home" page 
	 * of the application, where users can search and update their movie collection.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param session    session
	 * 
	 * @return home page
	 */
	@GetMapping("/")
	public String home(Model model, Principal principal) {
		model.addAttribute("searchForm", new SearchForm());
		return prepareHome(model, principal, null);
	}
	

	
	/**
	 * Search user's default/active movie collection.  This filters the list of movies in the user's default/active movie
	 * collection to those that match their search.
	 * 
	 * @param model       model
	 * @param principal   principal
	 * @param session     session
	 * @param searchForm  search form
	 * @param result      binding result
	 * 
	 * @return home page, filtered to only movies matching their search
	 */
	@RequestMapping("/movies/search")
	public String search(Model model, Principal principal,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		return prepareHome(model, principal, searchForm.getSearchString());
	}
	
	private String prepareHome(Model model, Principal principal, String searchString) {
		Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (!defaultMovieCollection.isPresent()) {
			// send them to collections so they can set a default movie collection
			return "redirect:/collections";
		}
		model.addAttribute("defaultMovieCollection", defaultMovieCollection.get());
		try {
			Set<Movie> movies = movieService.getMoviesForCollection(defaultMovieCollection.get().getId(), principal.getName());
			model.addAttribute("totalMoviesInCollection", movies.size());
			model.addAttribute("unlinkCount", movies.stream()
					.filter(movie -> FormatUtil.isBlank(movie.getAttribute(ImdbAttribute.IMDB_ID.getKey())))
					.count());
			if (FormatUtil.isNotBlank(searchString)) {
				movies = movieService.searchMoviesForCollection(defaultMovieCollection.get().getId(), searchString, principal.getName());
			}
			List<String> attrColumns = movieService.getTableColumnPreferences(principal.getName());
			model.addAttribute("movies", movies);
			model.addAttribute("attrFormatters", ViewUtil.getDataFormatters(movies, attrColumns));
			model.addAttribute("attrColumns", attrColumns);
			
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to retrieve movies for default movie collection.", e);
			Alerts.setErrorMessage(model, "Unable to get movies for the collection.");
		}
		return "movie/movies";		
	}
	
	/**
	 * AJAX method for returning the full details of a single movie in the user's movie collection.  Returned result is 
	 * an HTML fragment that can be loaded into a dialog.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param movieId    id of movie to get details for
	 * 
	 * @return page fragment for movie details
	 */
	@RequestMapping("/movies/movieDetails")
	public String movieDetails(Model model, Principal principal, @RequestParam String movieId) {
		try {
			Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
			model.addAttribute("defaultMovieCollection", defaultMovieCollection.get());
			Optional<Movie> movie = movieService.getMovie(movieId, principal.getName());
			if (movie.isPresent()) {
				model.addAttribute("movie", movie.get());
			}
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to load movie id " + movieId + " for user " + principal.getName(), e);
		}
		return "movie/movieDetails";
	}
	
	@RequestMapping("/movies/movieStatistics")
	public String movieStatistics(Model model, Principal principal, @RequestParam String movieId) {
		try {
			Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
			model.addAttribute("defaultMovieCollection", defaultMovieCollection.get());
			Set<Movie> movies = movieService.getMoviesForCollection(defaultMovieCollection.get().getId(), principal.getName());
			Optional<Movie> movie = movieService.getMovie(movieId, principal.getName());
			if (movie.isPresent()) {
				model.addAttribute("movie", movie.get());
				
			}
			MovieStatistics movieStatistics = new MovieStatistics(movies);
			Optional<DoubleStatistics> ratingStatistics = movieStatistics.getDoubleStatistics(ImdbAttribute.IMDB_RATING.getKey());
			Optional<LongStatistics> voteStatistics = movieStatistics.getLenientLongStatistics(ImdbAttribute.IMDB_VOTES.getKey());
			Optional<WordStatistics> genreStatistics = movieStatistics.getWordStatistics(ImdbAttribute.GENRE.getKey());
			if (ratingStatistics.isPresent()) {
				model.addAttribute("ratingStatistics", ratingStatistics.get());
			}
			if (voteStatistics.isPresent()) {
				model.addAttribute("voteStatistics", voteStatistics.get());
			}
			if (genreStatistics.isPresent()) {
				model.addAttribute("genreStatistics", genreStatistics.get());
				model.addAttribute("genresMostCommon", genreStatistics.get().getTopWordCounts(3).stream()
						.map(WordStatistics.WordCount::toString).collect(Collectors.joining(",")));
				model.addAttribute("genresLeastCommon", genreStatistics.get().getBottomWordCounts(3).stream()
						.map(WordStatistics.WordCount::toString).collect(Collectors.joining(",")));
				if (movie.isPresent()) {
					model.addAttribute("specificGenreCounts", genreStatistics.get().getWordCountsForWords(movie.get().getAttribute(ImdbAttribute.GENRE.getKey())));
				}
			}

		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to load movie id " + movieId + " for user " + principal.getName(), e);
		}
		return "movie/movieStatistics";		
	}
	
	/**
	 * Page to add a new movie to the movie collection (manual entry).
	 * 
	 * @param model  model
	 * 
	 * @return page to add a new movie to the movie collection
	 */
	@RequestMapping("/movies/addMovie")
	public String addMovie(Model model) {
		model.addAttribute("movieForm", new MovieForm());
		return "movie/addMovie";
	}
	
	/**
	 * Page to edit a movie in the movie collection.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param movieId    id of movie to edit
	 * 
	 * @return page to edit movie
	 */
	@RequestMapping("/movies/editMovie")
	public String editMovie(Model model, Principal principal, @RequestParam String movieId) {
		try {
			Optional<Movie> movie = movieService.getMovie(movieId, principal.getName());
			if (!movie.isPresent()) {
				throw new IllegalArgumentException("Movie ID " + movieId + " could not be retrieved.");
			}
			model.addAttribute("movieForm", new MovieForm(movie.get()));
		} catch (Exception e) {
			LOGGER.error("Unable to edit movie for ID: " + movieId, e);
			Alerts.setErrorMessage(model, "This movie cannot be edited.");
			return home(model, principal);
		}
		return "movie/editMovie";
	}
	
	/**
	 * Add a new movie to the movie collection (manual entry). 
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param movieForm  movie form
	 * @param result     binding result
	 * 
	 * @return home page
	 */
	@RequestMapping("/movies/addMovieSubmit")
	public String addMovieSubmit(Model model, Principal principal,
			@ModelAttribute("movieForm") @Valid MovieForm movieForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "movie/addMovie";
		}
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = movieForm.toMovie();
		movie.setCollectionId(movieCollection.get().getId());
		try {
			movieService.addMovie(movie, principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to add movie.", e);
			Alerts.setErrorMessage(model, "This movie cannot be added.");
			return home(model, principal);
		}
		Alerts.setMessage(model, "Movie added to the collection.");
		return home(model, principal);
	}
	
	/**
	 * Update a movie in the movie collection.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param movieForm  movie form
	 * @param result     binding result
	 * 
	 * @return home page
	 */
	@RequestMapping("/movies/editMovieSubmit")
	public String editMovieSubmit(Model model, Principal principal,
			@ModelAttribute("movieForm") @Valid MovieForm movieForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "movie/editMovie";
		}
		try {
			Movie movieBefore = movieService.getMovie(movieForm.getId(), principal.getName()).get();
			Movie movie = movieForm.toMovie();
			movie.setId(movieBefore.getId());
			movie.setCollectionId(movieBefore.getCollectionId());
			movieService.updateMovie(movie, principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to update movie.", e);
			Alerts.setErrorMessage(model, "This movie cannot be updated.");
			return home(model, principal);
		}
		Alerts.setMessage(model, "Movie updated.");
		return home(model, principal);
	}
	
	/**
	 * Delete movie from the movie collection.  Confirmation should be handled on client side.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param movieId    id of movie to delete
	 * 
	 * @return home page
	 */
	@RequestMapping(value="/movies/deleteMovie", method=RequestMethod.POST)
	public String deleteMovie(Model model, Principal principal, @RequestParam String movieId) {
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		try {
			Movie movie = movieService.getMovie(movieId, principal.getName()).get();
			if (!movie.getCollectionId().equals(movieCollection.get().getId())) {
				Alerts.setErrorMessage(model, "Movies can only be deleted from your currently active collection.");
				return home(model, principal);
			}
			movieService.deleteMovie(movieId, principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to delete movie.", e);
			Alerts.setErrorMessage(model, "Movie cannot be deleted.");
			return home(model, principal);
		}
		Alerts.setMessage(model, "Movie deleted.");
		return home(model, principal);
	}
	
	/**
	 * Page for configuring what columns of data are included in the table of movies listed on the home page.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * 
	 * @return page for configuring movie table columns
	 */
	@RequestMapping("/movies/configureColumns")
	public String configureColumns(Model model, Principal principal) {
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		List<String> tableColumnOptions = null;
		try {
			tableColumnOptions = movieService.getAttributeKeysForCollection(movieCollection.get().getId(), principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("User " + principal.getName() + " cannot configure columns for collection " + movieCollection.get().getId(), e);
			Alerts.setErrorMessage(model, "Columns cannot be configured.");
			return home(model, principal);
		}
		List<String> tableColumnPreferences = movieService.getTableColumnPreferences(principal.getName());
		tableColumnOptions.removeAll(tableColumnPreferences); // remove ones already in preference list
		model.addAttribute("tableColumnPreferences", tableColumnPreferences);
		model.addAttribute("tableColumnOptions", tableColumnOptions);
		return "movie/configureColumns";
	}
	
	/**
	 * Reorder columns shown on the home page table of movies using drag and drop indicies for columns.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param dragIndex  index of column being moved
	 * @param dropIndex  index of column where target column is being moved to
	 * 
	 * @return page for configuring movie table columns
	 */
	@RequestMapping("/movies/reorderColumns")
	public String reorderColumns(Model model, Principal principal, @RequestParam int dragIndex, @RequestParam int dropIndex) {
		LOGGER.debug("Requested drag from " + dragIndex + " to " + dropIndex);
		try {
			movieService.reorderTableColumnPreference(dragIndex, dropIndex, principal.getName());
			// not going to set success messages here as it would reduce usability of the interface and be of little value
		} catch (Exception e) {
			LOGGER.error("Unable to reorder columns.", e);
			Alerts.setErrorMessage(model, "Table columns could not be reordered.");
		}
		return configureColumns(model, principal);
	}
	
	/**
	 * Add a new attribute as a column in the table of movies shown on the home page.
	 * 
	 * @param model          model
	 * @param principal      principal
	 * @param attributeName  name of attribute to add as a column in the table
	 * 
	 * @return page for configuring movie table columns
	 */
	@RequestMapping("/movies/addColumnPreference")
	public String addColumnPreference(Model model, Principal principal, @RequestParam String attributeName) {
		movieService.addTableColumnPreference(attributeName, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal);
	}
	
	/**
	 * Remove a column from the table of movies shown on the home page.
	 * 
	 * @param model        model
	 * @param principal    principal
	 * @param deleteIndex  index of column to be removed
	 * 
	 * @return page for configuring movie table columns
	 */
	@RequestMapping("/movies/deleteColumnPreference")
	public String deleteColumnPreference(Model model, Principal principal, @RequestParam int deleteIndex) {
		movieService.deleteTableColumnPreference(deleteIndex, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal);		
	}
	
	/**
	 * AJAX generic request to dismiss a "session alert".  Once a session alert is dismissed, it will remain dismissed for the
	 * duration of the user's session.  Provides no response.
	 * 
	 * @param session  session
	 * @param key      key for alert
	 */
	@RequestMapping("/dismissSessionAlert")
	public @ResponseBody void dismissSessionAlert(HttpSession session, @RequestParam String key) {
		Alerts.dismissSessionAlert(session, key);
	}
}
