package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.xandercat.pmdb.dto.MovieStatistics;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.form.movie.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbAttribute;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.BootstrapStackedProgressBar;
import org.xandercat.pmdb.util.DoubleStatistics;
import org.xandercat.pmdb.util.LongStatistics;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.WordStatistics;
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
	
	/**
	 * AJAX method for returning movie and movie collection statistics for a movie in a public collection.  Returned result is
	 * an HTML fragment that can be loaded into a dialog.
	 * 
	 * @param model      model
	 * @param movieId    movie ID
	 * 
	 * @return page fragment for movie statistics
	 */
	@RequestMapping("/public/movieStatistics")
	public String movieStatistics(Model model, @RequestParam String movieId) {
		try {
			Optional<Movie> movie = movieService.getPublicMovie(movieId);
			MovieCollection defaultMovieCollection = collectionService.getPublicMovieCollection(movie.get().getCollectionId());
			model.addAttribute("defaultMovieCollection", defaultMovieCollection);
			Set<Movie> movies = movieService.getMoviesForCollection(defaultMovieCollection.getId(), defaultMovieCollection.getOwner());
			if (movie.isPresent()) {
				model.addAttribute("movie", movie.get());
				
			}
			MovieStatistics movieStatistics = new MovieStatistics(movies);
			Optional<DoubleStatistics> ratingStatistics = movieStatistics.getDoubleStatistics(ImdbAttribute.IMDB_RATING.getKey());
			Optional<LongStatistics> voteStatistics = movieStatistics.getLenientLongStatistics(ImdbAttribute.IMDB_VOTES.getKey());
			Optional<WordStatistics> genreStatistics = movieStatistics.getWordStatistics(ImdbAttribute.GENRE.getKey());
			if (ratingStatistics.isPresent()) {
				BootstrapStackedProgressBar pbar = new BootstrapStackedProgressBar(0.1, 10d);
				pbar.addBar(ratingStatistics.get().getMin(), "Min: ", "progress-bar-danger");
				pbar.addBar(ratingStatistics.get().getAverage(), "Average: ", "progress-bar-warning");
				pbar.addBar(ratingStatistics.get().getMax(), "Max: ", "progress-bar-success");
				model.addAttribute("ratingCollectionPBar", pbar.finalized());
				double movieRating = FormatUtil.parseDoubleLenient(movie.get().getAttribute(ImdbAttribute.IMDB_RATING.getKey()));
				String label = ratingStatistics.get().isOutlier(movieRating)? "Outlier: " : "";
				model.addAttribute("ratingMoviePBar", pbar.getComparisonBar(movieRating, label, null));
				
			}
			if (voteStatistics.isPresent()) {
				BootstrapStackedProgressBar pbar = new BootstrapStackedProgressBar(0.1);
				pbar.addBar(voteStatistics.get().getMin(), "Min: ", "progress-bar-danger");
				pbar.addBar(voteStatistics.get().getMedian(), "Median: ", "progress-bar-warning");
				pbar.addBar(voteStatistics.get().getMax(), "Max: ", "progress-bar-success");
				model.addAttribute("votesCollectionPBar", pbar.finalized());
				double movieVotes = FormatUtil.parseDoubleLenient(movie.get().getAttribute(ImdbAttribute.IMDB_VOTES.getKey()));
				String label = voteStatistics.get().isOutlier(Math.round(movieVotes))? "Outlier: " : "";
				model.addAttribute("votesMoviePBar", pbar.getComparisonBar(movieVotes, label, null));			
			}
			if (genreStatistics.isPresent()) {
				model.addAttribute("genreStatistics", genreStatistics.get());
				model.addAttribute("genresMostCommon", genreStatistics.get().getTopWordCounts(3).stream()
						.map(WordStatistics.WordCount::toString).collect(Collectors.joining(",")));
				model.addAttribute("genresLeastCommon", genreStatistics.get().getBottomWordCounts(3).stream()
						.map(WordStatistics.WordCount::toString).collect(Collectors.joining(",")));
				if (movie.isPresent()) {
					model.addAttribute("specificGenreCounts", genreStatistics.get()
							.getWordCountsForWords(movie.get().getAttribute(ImdbAttribute.GENRE.getKey())).stream()
							.map(WordStatistics.WordCount::toString).collect(Collectors.joining(",")));
				}
			}

		} catch (CollectionSharingException | WebServicesException e) {
			LOGGER.error("Unable to load movie id " + movieId + " for public user", e);
		}
		return "public/movieStatistics";		
	}
}
