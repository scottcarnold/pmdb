package org.xandercat.pmdb.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;

/**
 * Service interface for the management of movie collections.  Does not include methods for 
 * managing individual movies; for individual movie management, see {@link MovieService}.
 * 
 * @author Scott Arnold
 */
public interface CollectionService {

	public Optional<MovieCollection> getDefaultMovieCollection(String username);
	
	public void setDefaultMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public MovieCollection getViewableMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public List<MovieCollection> getViewableMovieCollections(String username);
	
	public List<MovieCollection> getShareOfferMovieCollections(String username);
	
	public void addMovieCollection(MovieCollection movieCollection, String callingUsername) throws WebServicesException;
	
	public void updateMovieCollection(MovieCollection movieCollection, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	public void deleteMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	public void shareMovieCollection(String collectionId, String shareWithUsername, boolean editable, String callingUsername) throws CollectionSharingException;
	
	public void unshareMovieCollection(String collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException;
	
	public void updateEditable(String collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException;
	
	public void acceptShareOffer(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public void declineShareOffer(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public MovieCollection assertCollectionViewable(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public MovieCollection assertCollectionEditable(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public List<CollectionPermission> getCollectionPermissions(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public Optional<CollectionPermission> getCollectionPermission(String collectionId, String username, String callingUsername) throws CollectionSharingException;
	
	public void importCollection(MultipartFile mFile, String collectionName, boolean cloud, List<String> sheetNames, List<String> columnNames, String callingUsername) throws IOException;
}
