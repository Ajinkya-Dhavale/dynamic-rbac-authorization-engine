package com.techpulse.rbac;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=test-only-secret-key-with-at-least-32-characters",
		"JWT_EXPIRATION_MS=3600000"
})
class DynamicRbacEngineApplicationTests {

	@Test
	void contextLoads() {
	}

}
