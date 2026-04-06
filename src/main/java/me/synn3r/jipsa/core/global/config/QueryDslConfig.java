package me.synn3r.jipsa.core.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Configuration
public class QueryDslConfig {

	private final EntityManager entityManager;

	public QueryDslConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
	}
}
