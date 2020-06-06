package org.xandercat.pmdb.dto.imdb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent an IMDB movie details result.
 * 
 * @author Scott Arnold
 */
@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieDetailsWrapper {

	@XmlElement(name="movie")
	private MovieDetails movieDetails;

	public MovieDetails getMovieDetails() {
		return movieDetails;
	}

	public void setMovieDetails(MovieDetails movieDetails) {
		this.movieDetails = movieDetails;
	}
}
