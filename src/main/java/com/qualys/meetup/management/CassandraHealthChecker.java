package com.qualys.meetup.management;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.qualys.meetup.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qualys.meetup.management.ServiceHealthCheck.DOWN_COUNT;
import static com.qualys.meetup.management.ServiceHealthCheck.STATUS;
import static com.qualys.meetup.management.ServiceHealthCheck.UP_COUNT;

@Service
public class CassandraHealthChecker {

    @Value("${management.cassandra.query}")
    private String healthCassandraQuery;

    private AtomicInteger cas_successCounter = new AtomicInteger(0);
    private AtomicInteger cas_failedCounter = new AtomicInteger(0);

    public boolean doHealthCheck(Map<String, Object> healthMap) {
        try {
            Session session = (Session) SpringContextUtil.getBean(Session.class);
            session.execute(healthCassandraQuery);
            healthMap.put(STATUS, HealthStatus.UP);
        } catch (InvalidQueryException ie) {
            healthMap.put(STATUS, HealthStatus.WRONG_HEALTH_CHECK_CONFIGURATION);
        } catch (Exception e) {
            healthMap.put(STATUS, HealthStatus.DOWN);
            healthMap.put(UP_COUNT, cas_successCounter.get());
            healthMap.put(DOWN_COUNT, cas_failedCounter.incrementAndGet());
            return false;
        }
        healthMap.put(UP_COUNT, cas_successCounter.incrementAndGet());
        healthMap.put(DOWN_COUNT, cas_failedCounter.get());
        return true;
    }
}
