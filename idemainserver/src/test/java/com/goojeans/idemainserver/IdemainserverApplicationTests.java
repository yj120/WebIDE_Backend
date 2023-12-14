package com.goojeans.idemainserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@Testcontainers
@SpringBootTest
class IdemainserverApplicationTests {

/*	@Container
	static JdbcDatabaseContainer testMysqlContainer = new MySQLContainer("mysql:8")
			.withDatabaseName("testdb");*/
	@Test
	void contextLoads() {
	}

}
