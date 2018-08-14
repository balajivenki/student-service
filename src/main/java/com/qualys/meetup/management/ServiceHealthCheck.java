package com.qualys.meetup.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ardas on 12/8/2017.
 */
@Slf4j
@Component
public class ServiceHealthCheck implements ReactiveHealthIndicator {

	public static final String STATUS = "STATUS";
	public static final String UP_COUNT = "UP_COUNT";
	public static final String DOWN_COUNT = "DOWN_COUNT";
	protected static final String ELASTIC = "elasticsearch";
	protected static final String CASSANDRA = "cassandra";
	private static final String CONSUL = "consul";

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.version}")
	private String applicationVersion;

	@Autowired
	private CassandraHealthChecker cassandraHealthChecker;

	@Autowired
	private ConsulHealthChecker consulHealthChecker;

	@Autowired
	private ElasticSearchHealthChecker elasticSearchHealthChecker;

	@Override
	public Mono<Health> health() {
		Health.Builder builder = new Health.Builder();
		builder.withDetail("name", applicationName);
		builder.withDetail("version", applicationVersion);
		boolean healthStatus = true;
		try {
			healthStatus = checkConsulHealth(builder, healthStatus);
			healthStatus = checkCassandraHealth(builder, healthStatus);
			healthStatus = checkElasticHealth(builder, healthStatus);
		} catch (Exception e) {
			builder.down(e);
			healthStatus = false;
			log.error("Health check is not working", e);
		}
		if (healthStatus) {
			log.info("Health check completed successfully");
		} else {
			log.info("Health check failed");
		}
		return Mono.just(builder.build());
	}

	private boolean checkConsulHealth(Health.Builder builder, boolean healthStatus) {
		Map<String, Object> healthMap = new LinkedHashMap<>();
		boolean status = consulHealthChecker.doHealthCheck(healthMap);
		updateHealthInfo(builder, healthStatus, status, CONSUL, healthMap);
		return status;
	}

	private boolean checkCassandraHealth(Health.Builder builder, boolean healthStatus) {
		Map<String, Object> healthMap = new LinkedHashMap<>();
		boolean status = cassandraHealthChecker.doHealthCheck(healthMap);
		updateHealthInfo(builder, healthStatus, status, CASSANDRA, healthMap);
		return status && healthStatus;
	}

	private boolean checkElasticHealth(Builder builder, boolean healthStatus) {
		Map<String, Object> healthMap = new LinkedHashMap<>();
		boolean status = elasticSearchHealthChecker.doHealthCheck(healthMap);
		updateHealthInfo(builder, healthStatus, status, ELASTIC, healthMap);
		return status && healthStatus;
	}

	private void updateHealthInfo(Health.Builder builder, boolean healthStatus, boolean status, String name, Map<String, Object> healthMap) {
		if (healthStatus && status) {
			builder.up();
		} else {
			builder.down();
		}
		builder.withDetail(name, healthMap);
		try {
		} catch (Exception e) {
			log.error("Failed to generate health json", e);
		}
	}
}
