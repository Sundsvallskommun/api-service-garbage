package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.garbage.Application;

@WireMockAppTestSuite(files = "classpath:/GarbageIT/", classes = Application.class)
@Sql({"/db/scripts/truncate.sql", "/db/scripts/testdata-it.sql"})
class GarbageIT extends AbstractAppTest {

	@Test
	void test1_getGarbage() {
		setupCall()
			.withServicePath("/2281/schedules")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_getGarbagePaginated() {
		// Page 2 with limit 1 must return the second grouped address with ALL its schedules,
		// not a single entity row. Regression test for entity-level pagination splitting groups.
		setupCall()
			.withServicePath("/2281/schedules?page=2&limit=1")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_getGarbageByLetterSuffix() {
		// Address with letter suffix ("5G"): the letter is in additionalInformation, not houseNumber.
		setupCall()
			.withServicePath("/2281/schedules?street=Storgatan&houseNumber=5&additionalInformation=G")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

}
