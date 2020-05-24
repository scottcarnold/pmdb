package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.FormattedMovie;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.PmdbException;
import org.xandercat.pmdb.form.movie.MovieForm;
import org.xandercat.pmdb.form.movie.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.format.Transformers;

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
	
	@GetMapping("/")
	public String home(Model model, Principal principal, HttpSession session) {
		boolean editMode = ViewUtil.isMoviesEditMode(session);
		model.addAttribute("searchForm", new SearchForm(editMode));
		return prepareHome(model, principal, null, editMode, session);
	}
	
	@RequestMapping("/movies/search")
	public String search(Model model, Principal principal, HttpSession session,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		return prepareHome(model, principal, searchForm.getSearchString(), searchForm.isEditMode(), session);
	}
	
	private String prepareHome(Model model, Principal principal, String searchString, boolean editMode, HttpSession session) {
		Optional<MovieCollection> defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (!defaultMovieCollection.isPresent()) {
			// send them to collections so they can set a default movie collection
			return "redirect:/collections";
		}
		ViewUtil.setMoviesEditMode(session, editMode);
		model.addAttribute("editMode", Boolean.valueOf(editMode));
		model.addAttribute("defaultMovieCollection", defaultMovieCollection.get());
		try {
			Set<Movie> movies = movieService.getMoviesForCollection(defaultMovieCollection.get().getId(), principal.getName());
			model.addAttribute("totalMoviesInCollection", movies.size());
			model.addAttribute("unlinkCount", movies.stream()
					.filter(movie -> StringUtils.isEmptyOrWhitespace(movie.getAttribute(ImdbSearchService.IMDB_ID_KEY)))
					.count());
			if (!StringUtils.isEmptyOrWhitespace(searchString)) {
				movies = movieService.searchMoviesForCollection(defaultMovieCollection.get().getId(), searchString, principal.getName());
			}
			List<String> attrColumns = movieService.getTableColumnPreferences(principal.getName());
			Set<FormattedMovie> formattedMovies = Transformers.getFormattedMovies(movies, attrColumns);
			model.addAttribute("movies", formattedMovies); 
			model.addAttribute("attrColumns", attrColumns);
			
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to retrieve movies for default movie collection.", e);
			Alerts.setErrorMessage(model, "Unable to get movies for the collection.");
		}
		return "movie/movies";		
	}
	
	@RequestMapping("/movies/addMovie")
	public String addMovie(Model model) {
		model.addAttribute("movieForm", new MovieForm());
		return "movie/addMovie";
	}
	
	@RequestMapping("/movies/editMovie")
	public String editMovie(Model model, Principal principal, @RequestParam String movieId, HttpSession session) {
		try {
			Optional<Movie> movie = movieService.getMovie(movieId, principal.getName());
			if (!movie.isPresent()) {
				throw new PmdbException("Movie ID " + movieId + " could not be retrieved.");
			}
			model.addAttribute("movieForm", new MovieForm(movie.get()));
		} catch (Exception e) {
			LOGGER.error("Unable to edit movie for ID: " + movieId, e);
			Alerts.setErrorMessage(model, "This movie cannot be edited.");
			return home(model, principal, session);
		}
		return "movie/editMovie";
	}
	
	@RequestMapping("/movies/addMovieSubmit")
	public String addMovieSubmit(Model model, Principal principal, HttpSession session,
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
			return home(model, principal, session);
		}
		Alerts.setMessage(model, "Movie added to the collection.");
		return home(model, principal, session);
	}
	
	@RequestMapping("/movies/editMovieSubmit")
	public String editMovieSubmit(Model model, Principal principal, HttpSession session,
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
			return home(model, principal, session);
		}
		Alerts.setMessage(model, "Movie updated.");
		return home(model, principal, session);
	}
	
	@RequestMapping(value="/movies/deleteMovie", method=RequestMethod.POST)
	public String deleteMovie(Model model, Principal principal, @RequestParam String movieId, HttpSession session) {
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		try {
			Movie movie = movieService.getMovie(movieId, principal.getName()).get();
			if (!movie.getCollectionId().equals(movieCollection.get().getId())) {
				Alerts.setErrorMessage(model, "Movies can only be deleted from your currently active collection.");
				return home(model, principal, session);
			}
			movieService.deleteMovie(movieId, principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to delete movie.", e);
			Alerts.setErrorMessage(model, "Movie cannot be deleted.");
			return home(model, principal, session);
		}
		Alerts.setMessage(model, "Movie deleted.");
		return home(model, principal, session);
	}
	
	@RequestMapping("/movies/configureColumns")
	public String configureColumns(Model model, Principal principal, HttpSession session) {
		Optional<MovieCollection> movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		List<String> tableColumnOptions = null;
		try {
			tableColumnOptions = movieService.getAttributeKeysForCollection(movieCollection.get().getId(), principal.getName());
		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("User " + principal.getName() + " cannot configure columns for collection " + movieCollection.get().getId(), e);
			Alerts.setErrorMessage(model, "Columns cannot be configured.");
			return home(model, principal, session);
		}
		List<String> tableColumnPreferences = movieService.getTableColumnPreferences(principal.getName());
		tableColumnOptions.removeAll(tableColumnPreferences); // remove ones already in preference list
		model.addAttribute("tableColumnPreferences", tableColumnPreferences);
		model.addAttribute("tableColumnOptions", tableColumnOptions);
		return "movie/configureColumns";
	}
	
	@RequestMapping("/movies/reorderColumns")
	public String reorderColumns(Model model, Principal principal, @RequestParam int dragIndex, @RequestParam int dropIndex, HttpSession session) {
		LOGGER.debug("Requested drag from " + dragIndex + " to " + dropIndex);
		try {
			movieService.reorderTableColumnPreference(dragIndex, dropIndex, principal.getName());
			// not going to set success messages here as it would reduce usability of the interface and be of little value
		} catch (PmdbException e) {
			LOGGER.error("Unable to reorder columns.", e);
			Alerts.setErrorMessage(model, "Table columns could not be reordered.");
		}
		return configureColumns(model, principal, session);
	}
	
	@RequestMapping("/movies/addColumnPreference")
	public String addColumnPreference(Model model, Principal principal, @RequestParam String attributeName, HttpSession session) {
		movieService.addTableColumnPreference(attributeName, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal, session);
	}
	
	@RequestMapping("/movies/deleteColumnPreference")
	public String deleteColumnPreference(Model model, Principal principal, @RequestParam int deleteIndex, HttpSession session) {
		movieService.deleteTableColumnPreference(deleteIndex, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal, session);		
	}
	
	@RequestMapping("/dismissSessionAlert")
	public void dismissSessionAlert(HttpSession session, @RequestParam String key) {
		Alerts.dismissSessionAlert(session, key);
	}
}
