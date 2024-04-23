package com.example.publicapi.controller;

import com.example.publicapi.component.KafkaConsumerService;
import com.example.publicapi.model.AdditionRecord;
import com.example.publicapi.model.AdditionRequest;
import com.example.publicapi.model.AdditionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@RestController
@RequestMapping()
public class AdditionEndpoint {

    @Autowired
    private WebClient webClientAddition;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addition(@RequestParam(name = "syncResult", required = true) boolean syncResult,
                                           @RequestBody(required = true) AdditionRequest body) throws JsonProcessingException {

        String result = webClientAddition.post()
                .uri("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new AdditionRequest(body.getNumberOne(), body.getNumberTwo())))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        if (syncResult) {
            AdditionRecord idObject = objectMapper.readValue(result, AdditionRecord.class);
            AdditionResponse additionResponse = kafkaConsumerService.waitForEntry(idObject.getAsyncId());
            return ResponseEntity.status(HttpStatus.OK).body(additionResponse.getCalculationResult().toString());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }

    @GetMapping("/list-results")
    @ResponseBody
    public ResponseEntity<AdditionResponse[]> getResults() {
        return ResponseEntity.status(HttpStatus.OK).body(kafkaConsumerService.getRecordsAsArray());
    }


}
