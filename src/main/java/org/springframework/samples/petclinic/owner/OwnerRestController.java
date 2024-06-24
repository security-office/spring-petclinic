package org.springframework.samples.petclinic.owner;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
public class OwnerRestController {
	private final OwnerRepository owners;

	public OwnerRestController(OwnerRepository clinicService) {
		this.owners = clinicService;
	}

	@GetMapping("/api/owners")
	Page<Owner> owners(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result) {
		// find owners by last name
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, "");
		if (ownersResults.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return null;
		}

		return ownersResults;
	}

	@PostMapping("/api/owner")
	public Owner processCreationForm(@Valid @RequestBody Owner owner, BindingResult result) {
		String country = owner.getCountry();
		if (country != null ) {
			Matcher match = Pattern.compile("^[a-zA-Z]{2}").matcher(country);
			if (!match.find()) {
				result.rejectValue("country", "notFound", "invalid country");
			}
		}

		String homepage = owner.getHomepage();
		if (homepage != null) {
			Matcher match = Pattern.compile("(http|https)://").matcher(homepage);
			if (!match.find()) {
				result.rejectValue("homepage", "notFound", "invalid homepage");
			}
		}

		if (result.hasErrors()) {
				return null;
		}

		this.owners.save(owner);
		return owner;
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 50;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastName(lastname, pageable);
	}
}
