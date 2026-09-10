package com.book_store.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "catalog.seed.enabled=false")
class CatalogApplicationTests {

	@Test
	void contextLoads() {
	}

}
