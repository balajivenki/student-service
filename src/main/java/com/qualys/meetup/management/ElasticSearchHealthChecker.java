package com.qualys.meetup.management;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qualys.meetup.management.ServiceHealthCheck.DOWN_COUNT;
import static com.qualys.meetup.management.ServiceHealthCheck.STATUS;
import static com.qualys.meetup.management.ServiceHealthCheck.UP_COUNT;

@Service
public class ElasticSearchHealthChecker {

    private AtomicInteger es_successCounter = new AtomicInteger(0);
    private AtomicInteger es_failedCounter = new AtomicInteger(0);

    @Value("${elasticsearch.hosts}")
    private String esHosts;

    @Value("${elasticsearch.http-port}")
    private String esPort;

    @Value("${management.elasticsearch.template.name}")
    private String esTemplate;

    public boolean doHealthCheck(Map<String, Object> healthMap) {
        boolean status = true;
        try {
            if (isTemplatePresent(healthMap)) {
                healthMap.put(STATUS, HealthStatus.UP);
            } else {
                healthMap.put(STATUS, HealthStatus.ELASTIC_TEMPLATE_MISSING);
            }
            healthMap.put(UP_COUNT, es_successCounter.incrementAndGet());
            healthMap.put(DOWN_COUNT, es_failedCounter.get());
        } catch (Exception e) {
            healthMap.put(STATUS, HealthStatus.DOWN);
            healthMap.put(UP_COUNT, es_successCounter.get());
            healthMap.put(DOWN_COUNT, es_failedCounter.incrementAndGet());
            status = false;
        }
        return status;
    }

    public boolean isTemplatePresent(Map<String, Object> healthMap) throws IOException {
        boolean status = true;
        String hostPorts[] = esHosts.split(",");
        for (String hostPort : hostPorts) {
            String host[] = hostPort.split(":");
            HttpClient client = new HttpClient();
            StringBuilder url = new StringBuilder("http://");
            url.append(host[0]).append(":").append(esPort).append("/_template/" + esTemplate);
            GetMethod method = new GetMethod(url.toString());
            if (client.executeMethod(method) != HttpStatus.SC_OK) {
                status = false;
                healthMap.put("isTemplatePresent -" + host[0], Boolean.toString(false));
                break;
            } else {
                healthMap.put("isTemplatePresent -" + host[0], Boolean.toString(true));
            }
        }
        return status;
    }
}
