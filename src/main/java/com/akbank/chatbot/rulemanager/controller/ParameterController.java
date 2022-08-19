package com.akbank.chatbot.rulemanager.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.akbank.chatbot.rulemanager.entity.KeyDescription;
import com.akbank.chatbot.rulemanager.mapper.model.ComparisonTypeDto;
import com.akbank.chatbot.rulemanager.mapper.model.KeyDescriptionDto;
import com.akbank.chatbot.rulemanager.service.KeyDescriptionService;
import com.akbank.chatbot.rulemanager.type.ComparisonType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/parameters")
public class ParameterController {
	private static Logger logger = LoggerFactory.getLogger(ParameterController.class);

	@Autowired
	KeyDescriptionService keyDescriptionService;

	@Autowired
	ModelMapper modelMapper;

	@GetMapping(value = "/comparisons")
	public ResponseEntity<List<ComparisonTypeDto>> getComparisonTypes() {
		List<ComparisonTypeDto> result = new ArrayList<>();
		for (ComparisonType type : ComparisonType.values()) {
			result.add(new ComparisonTypeDto(type.name(), type.getSign()));
		}
		logger.info("getComparisonTypes loaded:{}", result);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping(value = "/keys/{id}")
	public ResponseEntity<KeyDescriptionDto> getKey(
			@PathVariable @Parameter(name = "id", description = "key value", example = "1") Long id) {
		if (id > 0) {
			KeyDescription source = keyDescriptionService.findById(id);
			if (source != null) {
				KeyDescriptionDto result = modelMapper.map(source, KeyDescriptionDto.class);
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping(value = "/keys")
	public ResponseEntity<List<KeyDescriptionDto>> getAllAction() {
		List<KeyDescription> list = keyDescriptionService.findAll();
		if (list != null && !list.isEmpty()) {
			List<KeyDescriptionDto> listDto = modelMapper.map(list, new TypeToken<List<KeyDescriptionDto>>() {
			}.getType());
			return ResponseEntity.ok(listDto);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@Operation(summary = "Save key")
	@PostMapping(value = "/keys")
	public ResponseEntity<Object> save(@RequestBody KeyDescriptionDto dto) {
		if (dto != null) {
			try {
				Long id = keyDescriptionService.save(dto);
				if (id != null) {
					URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/parameters/keys/{id}")
							.buildAndExpand(id).toUri();
					logger.info("save completed-location {}", location.getPath());
					return ResponseEntity.created(location).build();
				} else {
					logger.error("save Error- Id returned null");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				}
			} catch (Exception e) {
				logger.error("save Error", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} else {
			logger.error("save Error- object is null");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@Operation(summary = "Update key")
	@PutMapping(value = "/keys/{id}")
	public ResponseEntity<Object> update(
			@PathVariable @Parameter(name = "id", description = "key id", example = "1") Long id,
			@RequestBody KeyDescriptionDto dto) {
		if (id == null) {
			logger.error("update Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		KeyDescription existing = keyDescriptionService.findById(id);
		if (existing != null) {
			if (existing.getId().equals(dto.getId())) {
				keyDescriptionService.update(existing, dto);
				return ResponseEntity.ok().body(true);
			} else {
				logger.error("update Error urlId:{} bodyId:{} not equals.", id, dto.getId());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
			}
		} else {
			logger.error("update Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

	@Operation(summary = "Delete key")
	@DeleteMapping(value = "/keys/{id}")
	public ResponseEntity<Boolean> delete(
			@PathVariable @Parameter(name = "id", description = "Key Id", example = "1") Long id) {
		if (id == null) {
			logger.error("delete Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		KeyDescription existing = keyDescriptionService.findById(id);
		if (existing != null) {
			keyDescriptionService.delete(existing);
			return ResponseEntity.ok().body(true);
		} else {
			logger.error("delete Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

}
