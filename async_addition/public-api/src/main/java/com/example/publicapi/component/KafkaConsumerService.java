package com.example.publicapi.component;

import com.example.publicapi.model.AdditionRecord;
import com.example.publicapi.model.AdditionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class KafkaConsumerService extends AbstractConsumerSeekAware {
    @Autowired
    private ObjectMapper objectMapper;

    private final HashMap<String, AdditionResponse> records = new HashMap<>();

    @KafkaListener(topics = "addition-service.results")
    public void onMessage(String kafkaRecord) throws JsonProcessingException {
        saveObject(kafkaRecord);
    }

    public synchronized void saveObject(String kafkaRecord) throws JsonProcessingException {
        AdditionRecord additionRecord = objectMapper.readValue(kafkaRecord, AdditionRecord.class);
        records.put(additionRecord.getAsyncId(), new AdditionResponse(additionRecord));
        notifyAll();
    }

    public synchronized AdditionResponse waitForEntry(String key) {
        while (!records.containsKey(key)) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return records.get(key);
    }

    public AdditionResponse[] getRecordsAsArray() {
        return records.values().toArray(AdditionResponse[]::new);
    }
}