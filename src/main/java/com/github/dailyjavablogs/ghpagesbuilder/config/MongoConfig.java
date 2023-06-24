package com.github.dailyjavablogs.ghpagesbuilder.config;

import com.github.dailyjavablogs.ghpagesbuilder.data.Identifiable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.util.UUID;

@Configuration
public class MongoConfig {

	@Bean
	public AbstractMongoEventListener<Identifiable<UUID>> uuidIdGenerator() {
		return new AbstractMongoEventListener<>() {
			@Override
			public void onBeforeConvert(BeforeConvertEvent<Identifiable<UUID>> event) {
				super.onBeforeConvert(event);
				if (event.getSource().getId() == null) {
					event.getSource().setId(UUID.randomUUID());
				}
			}
		};
	}
}
