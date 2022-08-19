package com.akbank.chatbot.rulemanager.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.akbank.chatbot.rulemanager.entity.AppAction;
import com.akbank.chatbot.rulemanager.entity.AppRule;
import com.akbank.chatbot.rulemanager.mapper.model.ActionDto;
import com.akbank.chatbot.rulemanager.mapper.model.RuleBag;
import com.akbank.chatbot.rulemanager.service.AppActionService;
import com.akbank.chatbot.rulemanager.util.RuleUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/actions")
public class RuleController {
	private static Logger logger = LoggerFactory.getLogger(RuleController.class);

	@Autowired
	AppActionService appActionService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	RuleUtil ruleUtil;

	@Operation(summary = "Get action with rules", responses = {
			@ApiResponse(description = "Action object", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
			@ApiResponse(responseCode = "404", description = "Action not found"),
			@ApiResponse(responseCode = "400", description = "Invalid ActionId") })

	@GetMapping(value = "/{id}")
	public ResponseEntity<ActionDto> getAction(
			@PathVariable @Parameter(name = "id", description = "ActionId value", example = "1") Long id) {
		if (id > 0) {
			AppAction action = appActionService.findById(id);
			if (action != null) {
				ActionDto result = modelMapper.map(action, ActionDto.class);
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping(value = "/mvel/{code}")
	public ResponseEntity<List<String>> getAsMvel(
			@PathVariable @Parameter(name = "code", description = "code", example = "1") String code) {
		if (StringUtils.isNotBlank(code)) {
			AppAction appAction = appActionService.findByCode(code);
			if (appAction != null) {
				List<String> result = new ArrayList<>();

				for (AppRule appRule : appAction.getRules()) {
					List<RuleBag> ruleList = ruleUtil.createRuleList(appRule);
					StringBuilder sb = new StringBuilder();
					if (!ruleList.isEmpty()) {
						int idx = 0;
						for (RuleBag bag : ruleList) {
							sb.append(bag.getMvelString());
							if (idx < ruleList.size()) {
								idx++;
								sb.append("&&");
							}
						}
					}
					result.add(sb.toString());
					sb.setLength(0);
				}
				return ResponseEntity.ok().body(result);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping
	public ResponseEntity<List<ActionDto>> getAllAction() {
		List<AppAction> actionList = appActionService.findAll();
		if (actionList != null && !actionList.isEmpty()) {

			List<ActionDto> actionListDto = modelMapper.map(actionList, new TypeToken<List<ActionDto>>() {
			}.getType());

			return ResponseEntity.ok(actionListDto);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@Operation(summary = "Save action with rules")
	@PostMapping
	public ResponseEntity<Object> save(@RequestBody ActionDto actionDto) {
		if (actionDto != null) {
			try {
				Long actionId = appActionService.save(actionDto);
				if (actionId != null) {
					URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/actions/{id}")
							.buildAndExpand(actionId).toUri();
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

	@Operation(summary = "Save action with rules")
	@PutMapping(value = "/{id}")
	public ResponseEntity<Object> update(
			@PathVariable @Parameter(name = "id", description = "ActionId value", example = "1") Long id,
			@RequestBody ActionDto actionDto) {
		if (id == null) {
			logger.error("update Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		AppAction action = appActionService.findById(id);
		if (action != null) {
			if (action.getId().equals(actionDto.getId())) {
				appActionService.update(action, actionDto);
				return ResponseEntity.ok().body(true);
			} else {
				logger.error("update Error urlId:{} bodyId:{} not equals.", id, actionDto.getId());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
			}
		} else {
			logger.error("update Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

	@Operation(summary = "Save action with rules")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Boolean> delete(
			@PathVariable @Parameter(name = "id", description = "ActionId value", example = "1") Long id) {
		if (id == null) {
			logger.error("delete Error, id is missing");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}

		AppAction action = appActionService.findById(id);
		if (action != null) {
			appActionService.delete(action);
			return ResponseEntity.ok().body(true);
		} else {
			logger.error("delete Error, id:{} not exists", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
	}

}
