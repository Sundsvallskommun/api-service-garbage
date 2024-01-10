package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.garbage.GarbageApplication;

@WireMockAppTestSuite(files = "classpath:/GarbageIT/", classes = GarbageApplication.class)
@Sql({"/db/scripts/truncate.sql", "/db/scripts/testdata-it.sql"})
class GarbageIT extends AbstractAppTest {

	@Test
	void test1_getGarbage() {
		setupCall()
			.withServicePath("/schedules")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

}
