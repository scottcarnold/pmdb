package org.xandercat.pmdb.controller;

import java.security.Principal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.form.movie.MovieForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.MovieService;
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
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (defaultMovieCollection == null) {
			// send them to collections so they can set a default movie collection
			return "redirect:/collections";
		}
		model.addAttribute("defaultMovieCollection", defaultMovieCollection);
		try {
			model.addAttribute("movies", movieService.getMoviesForCollection(defaultMovieCollection.getId(), principal.getName()));
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
		Movie movie = new Movie();
		movie.setTitle(movieForm.getTitle());
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
			Movie movie = movieService.getMovie(movieForm.getId(), principal.getName());
			movie.setTitle(movieForm.getTitle());
			movieService.updateMovie(movie, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to update movie.", e);
			ViewUtil.setErrorMessage(model, "This movie cannot be updated.");
			return home(model, principal);
		}
		ViewUtil.setMessage(model, "Movie updated.");
		return home(model, principal);
	}
	
	@RequestMapping("/movies/deleteMovie")
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
}
