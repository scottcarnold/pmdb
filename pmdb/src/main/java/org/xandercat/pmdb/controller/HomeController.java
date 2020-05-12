package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.form.movie.MovieForm;
import org.xandercat.pmdb.form.movie.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.PmdbException;
import org.xandercat.pmdb.util.ViewUtil;

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
	
	@GetMapping("/")
	public String home(Model model, Principal principal) {
		model.addAttribute("searchForm", new SearchForm());
		return prepareHome(model, principal, null);
	}
	
	@RequestMapping("/movies/search")
	public String search(Model model, Principal principal,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		return prepareHome(model, principal, searchForm.getSearchString());
	}
	
	private String prepareHome(Model model, Principal principal, String searchString) {
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (defaultMovieCollection == null) {
			// send them to collections so they can set a default movie collection
			return "redirect:/collections";
		}
		model.addAttribute("defaultMovieCollection", defaultMovieCollection);
		try {
			List<Movie> movies = movieService.getMoviesForCollection(defaultMovieCollection.getId(), principal.getName());
			model.addAttribute("totalMoviesInCollection", movies.size());
			if (!StringUtils.isEmptyOrWhitespace(searchString)) {
				movies = movieService.searchMoviesForCollection(defaultMovieCollection.getId(), searchString, principal.getName());
			}
			List<String> attrColumns = new ArrayList<String>();
			attrColumns.add("Rated");
			model.addAttribute("movies", movies); 
			model.addAttribute("attrColumns", attrColumns);
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to retrieve movies for default movie collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to get movies for the collection.");
		}
		return "movie/movies";		
	}
	
	@RequestMapping("/movies/addMovie")
	public String addMovie(Model model) {
		model.addAttribute("movieForm", new MovieForm());
		return "movie/addMovie";
	}
	
	@RequestMapping("/movies/editMovie")
	public String editMovie(Model model, Principal principal, @RequestParam int movieId) {
		try {
			Movie movie = movieService.getMovie(movieId, principal.getName());
			model.addAttribute("movieForm", new MovieForm(movie));
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to edit movie for ID: " + movieId, e);
			ViewUtil.setErrorMessage(model, "This movie cannot be edited.");
			return home(model, principal);
		}
		return "movie/editMovie";
	}
	
	@RequestMapping("/movies/addMovieSubmit")
	public String addMovieSubmit(Model model, Principal principal,
			@ModelAttribute("movieForm") @Valid MovieForm movieForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "movie/addMovie";
		}
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		Movie movie = movieForm.toMovie();
		movie.setCollectionId(movieCollection.getId());
		try {
			movieService.addMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to add movie.", e);
			ViewUtil.setErrorMessage(model, "This movie cannot be added.");
			return home(model, principal);
		}
		ViewUtil.setMessage(model, "Movie added to the collection.");
		return home(model, principal);
	}
	
	@RequestMapping("/movies/editMovieSubmit")
	public String editMovieSubmit(Model model, Principal principal,
			@ModelAttribute("movieForm") @Valid MovieForm movieForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "movie/editMovie";
		}
		try {
			Movie movieBefore = movieService.getMovie(movieForm.getId(), principal.getName());
			Movie movie = movieForm.toMovie();
			movie.setId(movieBefore.getId());
			movie.setCollectionId(movieBefore.getCollectionId());
			movieService.updateMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to update movie.", e);
			ViewUtil.setErrorMessage(model, "This movie cannot be updated.");
			return home(model, principal);
		}
		ViewUtil.setMessage(model, "Movie updated.");
		return home(model, principal);
	}
	
	@RequestMapping(value="/movies/deleteMovie", method=RequestMethod.POST)
	public String deleteMovie(Model model, Principal principal, @RequestParam int movieId) {
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		try {
			Movie movie = movieService.getMovie(movieId, principal.getName());
			if (movie.getCollectionId() != movieCollection.getId()) {
				ViewUtil.setErrorMessage(model, "Movies can only be deleted from your currently active collection.");
				return home(model, principal);
			}
			movieService.deleteMovie(movieId, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to delete movie.", e);
			ViewUtil.setErrorMessage(model, "Movie cannot be deleted.");
			return home(model, principal);
		}
		ViewUtil.setMessage(model, "Movie deleted.");
		return home(model, principal);
	}
	
	@RequestMapping("/movies/configureColumns")
	public String configureColumns(Model model, Principal principal) {
		MovieCollection movieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		List<String> tableColumnOptions = null;
		try {
			tableColumnOptions = movieService.getAttributeKeysForCollection(movieCollection.getId(), principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("User " + principal.getName() + " cannot configure columns for collection " + movieCollection.getId(), e);
			ViewUtil.setErrorMessage(model, "Columns cannot be configured.");
			return home(model, principal);
		}
		List<String> tableColumnPreferences = movieService.getTableColumnPreferences(principal.getName());
		tableColumnOptions.removeAll(tableColumnPreferences); // remove ones already in preference list
		model.addAttribute("tableColumnPreferences", tableColumnPreferences);
		model.addAttribute("tableColumnOptions", tableColumnOptions);
		return "movie/configureColumns";
	}
	
	@RequestMapping("/movies/reorderColumns")
	public String reorderColumns(Model model, Principal principal, @RequestParam int dragIndex, @RequestParam int dropIndex) {
		LOGGER.debug("Requested drag from " + dragIndex + " to " + dropIndex);
		try {
			movieService.reorderTableColumnPreference(dragIndex, dropIndex, principal.getName());
			// not going to set success messages here as it would reduce usability of the interface and be of little value
		} catch (PmdbException e) {
			LOGGER.error("Unable to reorder columns.", e);
			ViewUtil.setErrorMessage(model, "Table columns could not be reordered.");
		}
		return configureColumns(model, principal);
	}
	
	@RequestMapping("/movies/addColumnPreference")
	public String addColumnPreference(Model model, Principal principal, @RequestParam String attributeName) {
		movieService.addTableColumnPreference(attributeName, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal);
	}
	
	@RequestMapping("/movies/deleteColumnPreference")
	public String deleteColumnPreference(Model model, Principal principal, @RequestParam int deleteIndex) {
		movieService.deleteTableColumnPreference(deleteIndex, principal.getName());
		// not going to set success messages here as it would reduce usability of the interface and be of little value
		return configureColumns(model, principal);		
	}
}
