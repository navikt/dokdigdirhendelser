package no.nav.dokdigdirhendelser.config;

import no.altinn.event.domain.CloudEvent;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class KafkaConfig {

	@Bean
	public ProducerFactory<String, CloudEvent> producerFactory(KafkaProperties kafkaProperties, JsonMapper jsonMapper) {
		var factory = new DefaultKafkaProducerFactory<String, CloudEvent>(kafkaProperties.buildProducerProperties());
		factory.setKeySerializer(new StringSerializer());
		var serializer = new JacksonJsonSerializer<CloudEvent>(jsonMapper);
		serializer.setAddTypeInfo(false);
		factory.setValueSerializer(serializer);
		return factory;
	}

	@Bean
	public KafkaTemplate<String, CloudEvent> kafkaTemplate(ProducerFactory<String, CloudEvent> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}
}
