# student-service
Demonstration of how we can connect to cassandra, elasticsearch using spring boots 2

1. Run the cassandra script `db.cql`
2. Run the elastic script `students-index-mapping.json` via PostMan.
   PUT : localhost:9200/students
3. Configure the student-service.yml in Consul in some path lets `arpan/meetup/student-service.yml`.
   Give the consul location via VM argument while running the application:
   -Dspring.cloud.consul.host=qconsul.p08.eng.sjc01.qualys.com
   -Dspring.cloud.consul.port=80
   -Dspring.cloud.consul.config.prefix=arpan/meetup

4. Access the application : http://localhost:8900/api/swagger-ui.htm
