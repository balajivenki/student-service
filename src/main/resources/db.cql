create keyspace spring_demo with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } and durable_writes = true;
use spring_demo;

CREATE TYPE address (
street text,
city text,
country text,
pincode bigint
);

CREATE TABLE student (
student_id uuid,
name text,
address FROZEN<address>,
email text,
PRIMARY KEY (student_id, name)
);
