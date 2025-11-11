package io.hpp.noosphere.scheduler.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.hpp.noosphere.scheduler.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
