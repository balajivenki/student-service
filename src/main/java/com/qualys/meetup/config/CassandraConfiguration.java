package com.qualys.meetup.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;
import info.archinnov.achilles.generated.ManagerFactory;
import info.archinnov.achilles.generated.ManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.List;

@Configuration
public class CassandraConfiguration {

    @Value("${cassandra.host}")
    private String cassandraHost;

    @Value("${cassandra.cluster.pooling.maxThread}")
    private int maxThread;

    @Value("${cassandra.cluster.pooling.timeout}")
    private int timeout;

    @Value("${cassandra.keyspaces.keyspace.name}")
    private String defaultKeyspaceName;

    @Value("${cassandra.keyspaces.keyspace.readConsistency}")
    private String defaultReadConsistencyLevel;

    @Value("${cassandra.keyspaces.keyspace.writeConsistency}")
    private String defaultWriteConsistencyLevel;

    @Value("${cassandra.cluster.user}")
    private String userName;

    @Value("${cassandra.cluster.password}")
    private String password;

    private Session cassandraSession;

    @Bean(name = "cassandraSession")
    public Session cassandraSession() {
        if (cassandraSession == null) {
            cassandraNativeClusterProduction();
        }

        return cassandraSession;
    }

    @Bean(destroyMethod = "shutDown")
    public ManagerFactory cassandraNativeClusterProduction() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, maxThread);
        poolingOptions.setPoolTimeoutMillis(timeout);
        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, maxThread);
        PlainTextAuthProvider authProvider = new PlainTextAuthProvider(userName, password);
        Cluster cluster = Cluster.builder()
                .addContactPointsWithPorts(convertToInternetAddress())
                .withAuthProvider(authProvider)
                .withPoolingOptions(poolingOptions)
                .build();
        final ManagerFactory factory = ManagerFactoryBuilder
                .builder(cluster).withDefaultKeyspaceName(defaultKeyspaceName)
                .withDefaultReadConsistency(ConsistencyLevel.valueOf(defaultReadConsistencyLevel))
                .withDefaultWriteConsistency(ConsistencyLevel.valueOf(defaultWriteConsistencyLevel))
                .build();
        cassandraSession = cluster.connect(defaultKeyspaceName);
        return factory;
    }

    private List<InetSocketAddress> convertToInternetAddress() {
        List<InetSocketAddress> cassandraHosts = Lists.newArrayList();
        for (String host : cassandraHost.split(",")) {
            InetSocketAddress socketAddress = new InetSocketAddress(host.split(":")[0], Integer.valueOf(host.split(":")[1]));
            cassandraHosts.add(socketAddress);
        }
        return cassandraHosts;
    }
}