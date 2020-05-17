package org.xandercat.pmdb.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.form.collection.CollectionForm;
import org.xandercat.pmdb.form.collection.ExportForm;
import org.xandercat.pmdb.form.collection.ExportType;
import org.xandercat.pmdb.form.collection.ShareCollectionForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.ExcelPorter;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class CollectionController {

	private static final Logger LOGGER = LogManager.getLogger(CollectionController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private MovieService movieService;
	
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
		List<MovieCollection> shareOffers = collectionService.getShareOfferMovieCollections(username);
		model.addAttribute("movieCollections", movieCollections);
		if (shareOffers.size() > 0) {
			model.addAttribute("shareOffers", shareOffers);
		}
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
				return "redirect:/";  // after setting a new default collection, immediately go to movie list for that collection
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
			return "redirect:/";  // after setting a new default collection, immediately go to movie list for that collection			
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to set default movie collection", e);
			ViewUtil.setErrorMessage(model, "Default movie collection could not be set.");
			return collections(model, principal);
		}
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
		try {
			MovieCollection movieCollection = collectionService.getViewableMovieCollection(collectionId, principal.getName());
			List<CollectionPermission> collectionPermissions = collectionService.getCollectionPermissions(collectionId, principal.getName());
			model.addAttribute("movieCollection", movieCollection);
			model.addAttribute("collectionPermissions", collectionPermissions);
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to retrieve collection permissions for collection " + collectionId + " user " + principal.getName(), e);
			ViewUtil.setErrorMessage(model, "Sharing is not available on the collection.");
			return collections(model, principal);
		}
		return "collection/editSharing";
	}
	
	@RequestMapping("/collections/acceptShareOffer")
	public String acceptShareOffer(Model model, Principal principal, @RequestParam int collectionId, HttpSession session) {
		try {
			collectionService.acceptShareOffer(collectionId, principal.getName());
			ViewUtil.updateNumShareOffers(collectionService, session, principal.getName());
			ViewUtil.setMessage(model, "Share offer accepted.");
		} catch (CollectionSharingException e) {
			ViewUtil.setErrorMessage(model, "Unable to accept share offer.");
		}
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/declineShareOffer")
	public String declineShareOffer(Model model, Principal principal, @RequestParam int collectionId, HttpSession session) {
		try {
			collectionService.declineShareOffer(collectionId, principal.getName());
			ViewUtil.updateNumShareOffers(collectionService, session, principal.getName());
			ViewUtil.setMessage(model, "Share offer declined.");
		} catch (CollectionSharingException e) {
			ViewUtil.setErrorMessage(model, "Unable to decline share offer.");
		}
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/toggleEditPermission")
	public String toggleEditPermission(Model model, Principal principal, @RequestParam int collectionId, @RequestParam String username) {
		try {
			CollectionPermission permission = collectionService.getCollectionPermission(collectionId, username, principal.getName());
			if (permission == null) {
				throw new CollectionSharingException("Unable to obtain permission for collection " + collectionId + " user " + username);
			}
			collectionService.updateEditable(collectionId, username, !permission.isAllowEdit(), principal.getName());
			ViewUtil.setMessage(model, "Edit permission updated.");
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to update edit permission for user.", e);
			ViewUtil.setErrorMessage(model, "Could not update edit permission for user.");
		}
		return editSharing(model, principal, collectionId);		
	}
	
	@RequestMapping(value="/collections/revokePermission", method=RequestMethod.POST)
	public String revokePermission(Model model, Principal principal, @RequestParam int collectionId, @RequestParam String username) {
		try {
			collectionService.unshareMovieCollection(collectionId, username, principal.getName());
			ViewUtil.setMessage(model, "Share revoked.");
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to revoke share.", e);
			ViewUtil.setErrorMessage(model, "Share could not be revoked.");
		}
		return editSharing(model, principal, collectionId);
	}
	
	@RequestMapping(value="/collections/revokeMyPermission", method=RequestMethod.POST)
	public String revokeMyPermission(Model model, Principal principal, @RequestParam int collectionId) {
		try {
			collectionService.unshareMovieCollection(collectionId, principal.getName(), principal.getName());
			ViewUtil.setMessage(model, "Removed access to collection.");
		} catch (CollectionSharingException e) {
			LOGGER.error("User unable to remove their own permission to a collection.", e);
			ViewUtil.setErrorMessage(model, "Access cannot be removed.");
		}
		return collections(model, principal);
	}
	
	@RequestMapping("/collections/shareCollection")
	public String shareCollection(Model model, Principal principal, @RequestParam int collectionId) {
		try {
			MovieCollection movieCollection = collectionService.getViewableMovieCollection(collectionId, principal.getName());
			model.addAttribute("movieCollection", movieCollection);
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to share movie collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to share movie collection.");
			return editSharing(model, principal, collectionId);
		}
		model.addAttribute("shareCollectionForm", new ShareCollectionForm(collectionId));
		return "collection/shareCollection";
	}
	
	@RequestMapping("/collections/shareCollectionSubmit")
	public String shareCollectionSubmit(Model model, Principal principal,
			@ModelAttribute("shareCollectionForm") @Valid ShareCollectionForm shareCollectionForm,
			BindingResult result) {
		PmdbUser shareWithUser = userService.getUser(shareCollectionForm.getUsernameOrEmail());
		if (shareWithUser == null) {
			shareWithUser = userService.getUserByEmail(shareCollectionForm.getUsernameOrEmail());
		}
		if (shareWithUser == null) {
			//TODO: Future educational activity -- see about using SpringConstraintValidationFactory to create a Spring wired validator for this
			result.rejectValue("usernameOrEmail", "{validation.UserReference.message}", "Invalid user reference.");
		}
		if (result.hasErrors()) {
			MovieCollection movieCollection = null;
			try {
				movieCollection = collectionService.getViewableMovieCollection(shareCollectionForm.getCollectionId(), principal.getName());
			} catch (CollectionSharingException e) {
			}
			model.addAttribute("movieCollection", movieCollection);
			return "collection/shareCollection";
		}
		try {
			collectionService.shareMovieCollection(shareCollectionForm.getCollectionId(), 
					shareWithUser.getUsername(), shareCollectionForm.isEditable(), principal.getName());
			ViewUtil.setMessage(model, "Offer sent to share collection.");
		} catch (CollectionSharingException e) {
			LOGGER.error("Unable to share collection.", e);
			ViewUtil.setErrorMessage(model, "Unable to share collection.");
		}
		return editSharing(model, principal, shareCollectionForm.getCollectionId());
	}
	
	@RequestMapping("/collections/export")
	public String export(Model model, Principal principal) {
		List<MovieCollection> movieCollections = collectionService.getViewableMovieCollections(principal.getName());
		if (movieCollections.size() == 0) {
			ViewUtil.setErrorMessage(model, "There are no collections that can be exported.");
			return collections(model, principal);
		}
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		int exportCollectionId = movieCollections.get(0).getId();
		if (defaultMovieCollection != null) {
			exportCollectionId = defaultMovieCollection.getId();
		}
		model.addAttribute("exportForm", new ExportForm(exportCollectionId, ExportType.XLSX));
		model.addAttribute("collectionOptions", ViewUtil.getOptions(movieCollections, "id", "name"));
		model.addAttribute("typeOptions", ViewUtil.getOptions(ExportType.class));
		return "collection/export";
	}
	
	@RequestMapping(value="/collections/exportSubmit", method=RequestMethod.POST)
	public String exportSubmit(Model model, Principal principal,
			@ModelAttribute("exportForm") ExportForm exportForm, HttpServletResponse response) {
		if (exportForm.getCollections() == null || exportForm.getCollections().size() == 0) {
			ViewUtil.setErrorMessage(model, "You must select at least 1 collection to export.");
			return export(model, principal);
		}
		try {
			ExportType exportType = ExportType.valueOf(exportForm.getType());
			ExcelPorter.Format format = ExcelPorter.Format.XLSX;
			if (exportType == ExportType.XLS) {
				format = ExcelPorter.Format.XLS;
			}  
			ExcelPorter excelPorter = new ExcelPorter(format);
			response.setContentType(excelPorter.getContentType());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + excelPorter.getFilename("PMDBExport") + "\"");
			List<String> collectionIdStrings = exportForm.getCollections();
			for (String collectionIdString : collectionIdStrings) {
				int collectionId = Integer.parseInt(collectionIdString);
				MovieCollection movieCollection = collectionService.getViewableMovieCollection(collectionId, principal.getName());
				Set<Movie> movies = movieService.getMoviesForCollection(collectionId, principal.getName());
				List<String> attributeKeys = movieService.getAttributeKeysForCollection(collectionId, principal.getName());
				excelPorter.addSheet(movieCollection, movies, attributeKeys);
			}
			excelPorter.closeWorkbook(response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			LOGGER.error("Unable to export collections to Excel.", e);
		}
		return export(model, principal);
	}
	
	@RequestMapping("/collections/import")
	public String importCollections(Model model, Principal principal) {
		ViewUtil.setErrorMessage(model, "Import functionality has not been implemented yet.");
		return collections(model, principal);
		//return "collection/import";
	}
}
