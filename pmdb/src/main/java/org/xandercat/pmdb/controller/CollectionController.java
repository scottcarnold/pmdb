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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.form.collection.CollectionForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class CollectionController {

	private static final Logger LOGGER = LogManager.getLogger(CollectionController.class);
			
	@Autowired
	private CollectionService collectionService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_COLLECTIONS;
	}

	@RequestMapping("/collections")
	public String collections(Model model, Principal principal) {
		String username = principal.getName();
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(username);
		if (defaultMovieCollection != null) {
			model.addAttribute("defaultMovieCollection", defaultMovieCollection);
		}
		List<MovieCollection> movieCollections = collectionService.getViewableMovieCollections(username);
		model.addAttribute("movieCollections", movieCollections);
		return "collection/collections";
	}
	
	@RequestMapping("/collections/addCollection")
	public String addCollection(Model model) {
		model.addAttribute("collectionForm", new CollectionForm());
		return "collection/addCollection";
	}
	
	@RequestMapping("/collections/addCollectionSubmit")
	public String addCollectionSubmit(Model model, Principal principal,
			@ModelAttribute("collectionForm") @Valid CollectionForm collectionForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "collection/addCollection";
		}
		List<MovieCollection> movieCollections = collectionService.getViewableMovieCollections(principal.getName());
		MovieCollection movieCollection = new MovieCollection();
		movieCollection.setName(collectionForm.getName());
		collectionService.addMovieCollection(movieCollection, principal.getName());
		if (movieCollections.size() == 0) {
			// user had no viewable movie collections; set the newly created collection as default
			try {
				collectionService.setDefaultMovieCollection(movieCollection.getId(), principal.getName());
			} catch (CollectionSharingException e) {
				LOGGER.error("Unable to set newly created movie collection as default.  This shouldn't happen.", e);
				// despite that this represents an unsettling error, there is no real value in notifying the user, so not setting message here
			}
		}
		ViewUtil.setMessage(model, "Movie collection added.");
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/changeDefaultCollection")
	public String changeDefaultCollection(Model model, Principal principal, @RequestParam int collectionId) {
		try {
			collectionService.setDefaultMovieCollection(collectionId, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to set default movie collection", e);
			ViewUtil.setErrorMessage(model, "Default movie collection could not be set.");
		}
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/editCollection")
	public String editCollection(Model model, Principal principal, @RequestParam int collectionId) {
		MovieCollection movieCollection = null;
		try {
			movieCollection = collectionService.getViewableMovieCollection(collectionId, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to edit movie collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to edit requested movie collection.");
			return collections(model, principal);
		}
		CollectionForm collectionForm = new CollectionForm(movieCollection);
		model.addAttribute("collectionForm", collectionForm);
		return "collection/editCollection";
	}

	@RequestMapping("/collections/editCollectionSubmit")
	public String editCollectionSubmit(Model model, Principal principal,
			@ModelAttribute("collectionForm") @Valid CollectionForm collectionForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "collection/editCollection";
		}
		MovieCollection movieCollection = new MovieCollection();
		movieCollection.setId(collectionForm.getId());
		movieCollection.setName(collectionForm.getName());
		try {
			collectionService.updateMovieCollection(movieCollection, principal.getName());
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to save collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to save movie collection.");
			return collections(model, principal);
		}
		ViewUtil.setMessage(model, "Movie collection saved.");
		return collections(model, principal);
	}
	
	@RequestMapping(value="/collections/deleteCollection", method=RequestMethod.POST)
	public String deleteCollection(Model model, Principal principal, @RequestParam int collectionId) {
		try {
			collectionService.deleteMovieCollection(collectionId, principal.getName());
			
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to delete collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to delete movie collection.");
			return collections(model, principal);
		}
		ViewUtil.setMessage(model, "Movie collection deleted.");
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/editSharing")
	public String editSharing(Model model, Principal principal, @RequestParam int collectionId) {
		ViewUtil.setErrorMessage(model, "This function has not yet been implemented in the user interface. In the meantime, an Administrator can set up sharing for you by request.");
		return collections(model, principal);
	}
}
