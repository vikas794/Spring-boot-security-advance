package com.example.security.apisecurity.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "security.modules.rate-limit.enabled=true",
        "security.modules.rate-limit.capacity=2",
        "security.modules.rate-limit.refill-tokens=2",
        "security.modules.rate-limit.refill-duration-seconds=60"
})
class RateLimitTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
}
