package com.bank.transaction_service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected static ClientAndServer mockServer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
    }

//    @BeforeEach
//    public void cleanupDatabase() {
//        jdbcTemplate.execute("DELETE FROM transactions");
//        jdbcTemplate.execute("DELETE FROM limits");
//        jdbcTemplate.execute("DELETE FROM exchange_rates");
//    }
}