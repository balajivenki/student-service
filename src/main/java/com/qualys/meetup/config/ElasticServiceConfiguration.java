package com.qualys.meetup.config;

import com.qualys.meetup.exception.ServiceException;
import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

@Configuration
public class ElasticServiceConfiguration {

    @Value("${elasticsearch.hosts}")
    private String elasticsearchHosts;

    @Value("${elasticsearch.http-port}")
    private String elasticsearchHttpPort;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.client.transport.ping_timeout}")
    private String transportPingTimeout;

    @Value("${elasticsearch.client.transport.nodes_sampler_interval}")
    private String transportNodeSamplerInterval;

    @Bean
    public Client client() {
        RestTemplate restTemplate = new RestTemplate();

        String elasticsearchClusterName = null;

        for (String host : elasticsearchHosts.split(",")) {
            try {
                String[] pairs = host.split(":");
                String elasticsearchHttpUrl = "http://" + pairs[0] + ":" + elasticsearchHttpPort;
                ResponseEntity elasticServerResponse = restTemplate.exchange(elasticsearchHttpUrl, HttpMethod.GET, new HttpEntity<>(createHeaders(username, password)), Map.class);
                Map serverDetails = (Map)elasticServerResponse.getBody();
                elasticsearchClusterName = (String) serverDetails.get("cluster_name");

                break;
            } catch (Exception e) {
                throw new ServiceException(e.getMessage(), e);
            }
        }

        if(elasticsearchClusterName == null) {
            throw new ServiceException("Unable to connect elasticsearch servers using http port " + elasticsearchHttpPort);
        }

        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("cluster.name", elasticsearchClusterName);
        settingsBuilder.put("client.transport.ping_timeout", transportPingTimeout);
        settingsBuilder.put("client.transport.nodes_sampler_interval", transportNodeSamplerInterval);
        TransportClient client = new PreBuiltTransportClient(settingsBuilder.build());

        for (String host : elasticsearchHosts.split(",")) {
            String[] pairs = host.split(":");
            int port = pairs.length == 2 ? Integer.valueOf(pairs[1]) : 9300;
            try {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(pairs[0]), port));
            } catch (UnknownHostException e) {
                throw new ServiceException("Error in connecting to Elasticsearch " + host, e);
            }
        }

        return client;
    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
}
