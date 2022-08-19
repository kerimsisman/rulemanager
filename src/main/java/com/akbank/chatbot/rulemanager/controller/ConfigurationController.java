package com.akbank.chatbot.rulemanager.controller;

import java.net.URI;
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

import com.akbank.chatbot.rulemanager.entity.Configuration;
import com.akbank.chatbot.rulemanager.mapper.model.ConfigurationDto;
import com.akbank.chatbot.rulemanager.service.ConfigurationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/configurations")
public class ConfigurationController {
	private static Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	ModelMapper modelMapper;

	@GetMapping
	public ResponseEntity<List<ConfigurationDto>> getAll() {
		List<Configuration> list = configurationService.findAll();
		if (list != null && !list.isEmpty()) {

			List<ConfigurationDto> listDto = modelMapper.map(list, new TypeToken<List<ConfigurationDto>>() {
			}.getType());

			return ResponseEntity.ok(listDto);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@Operation(summary = "Save configraution")
	@PostMapping
	public ResponseEntity<Object> save(@RequestBody ConfigurationDto ConfigurationDto) {
		if (ConfigurationDto != null) {
			try {
				Long newId = configurationService.save(ConfigurationDto);
				if (newId != null) {
					URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/actions/{id}")
							.buildAndExpand(newId).toUri();
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

	@Operation(summary = "Update configraution")
	@PutMapping(value = "/{id}")
	public ResponseEntity<Object> update(
			@PathVariable @Parameter(name = "id", description = "Unique Id", example = "1") Long id,
			@RequestBody ConfigurationDto ConfigurationDto) {
		if (id == null) {
			logger.error("update Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		Configuration existing = configurationService.findById(id);
		if (existing != null) {
			if (existing.getId().equals(ConfigurationDto.getId())) {
				configurationService.update(existing, ConfigurationDto);
				return ResponseEntity.ok().body(true);
			} else {
				logger.error("update Error urlId:{} bodyId:{} not equals.", id, ConfigurationDto.getId());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
			}
		} else {
			logger.error("update Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

	@Operation(summary = "Save configuration")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Boolean> delete(
			@PathVariable @Parameter(name = "id", description = "Unique Id", example = "1") Long id) {
		if (id == null) {
			logger.error("delete Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		Configuration existing = configurationService.findById(id);
		if (existing != null) {
			configurationService.delete(existing);
			return ResponseEntity.ok().body(true);
		} else {
			logger.error("delete Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}
}
