package com.example.userapi.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreated(String jsonPayload) {
        kafkaTemplate.send("user_created", jsonPayload);
        System.out.println("Evento enviado a Kafka: " + jsonPayload);
    }
}
