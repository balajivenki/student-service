package com.qualys.meetup.management;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Self;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qualys.meetup.management.ServiceHealthCheck.DOWN_COUNT;
import static com.qualys.meetup.management.ServiceHealthCheck.STATUS;
import static com.qualys.meetup.management.ServiceHealthCheck.UP_COUNT;

@Service
public class ConsulHealthChecker {

	private AtomicInteger consul_successCounter = new AtomicInteger(0);
	private AtomicInteger consul_failedCounter = new AtomicInteger(0);

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ConsulClient consulClient;

	public boolean doHealthCheck(Map<String, Object> healthMap) {
		try {
			Response e = consulClient.getAgentSelf();
			Self.Config config = ((Self) e.getValue()).getConfig();
			Response services = consulClient.getCatalogServices(QueryParams.DEFAULT);

			healthMap.put(STATUS, HealthStatus.UP);
			healthMap.put(UP_COUNT, consul_successCounter.incrementAndGet());
			healthMap.put(DOWN_COUNT, consul_failedCounter.get());

			if (services.getValue() != null) {
				Object object = ((Map) services.getValue()).get(applicationName);
				healthMap.put(applicationName, object);
			}
			healthMap.put("nodeName", config.getNodeName());
			healthMap.put("datacenter", config.getDatacenter());
		} catch (Exception e) {
			healthMap.put(STATUS, HealthStatus.DOWN);
			healthMap.put(UP_COUNT, consul_successCounter.get());
			healthMap.put(DOWN_COUNT, consul_failedCounter.incrementAndGet());
			return false;
		}
		return true;
	}
}
