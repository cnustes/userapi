package com.example.userapi.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "user_created", groupId = "userapi-group")
    public void listenUserCreated(ConsumerRecord<String, String> record) {
        System.out.println("📥 Evento recibido en Kafka:");
        System.out.println("🔑 Key: " + record.key());
        System.out.println("📦 Value: " + record.value());
    }
}
