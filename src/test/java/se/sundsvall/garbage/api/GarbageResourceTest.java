package se.sundsvall.garbage.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleRequest;
import static se.sundsvall.garbage.TestDataFactory.buildGarbageScheduleResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.garbage.Application;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.api.model.enums.FacilityCategory;
import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;
import se.sundsvall.garbage.service.GarbageService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class GarbageResourceTest {

	@MockBean
	private GarbageService service;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getGarbage() {
		final var response = buildGarbageScheduleResponse();
		final var request = buildGarbageScheduleRequest();

		when(service.getGarbageSchedules(any())).thenReturn(response);

		final var result = webTestClient.get()
			.uri("/schedules?street=%s&postalCode=%s&houseNumber=%s&city=%s".formatted(request.getStreet(), request.getPostalCode(), request.getHouseNumber(), request.getCity()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(GarbageScheduleResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(result).isNotNull();
		assertThat(request.getPage()).isEqualTo(1);
		assertThat(request.getLimit()).isEqualTo(10);

		final var firstResult = result.getFirst();
		assertThat(firstResult.getAdditionalInformation()).isNull();
		assertThat(firstResult.getGarbageScheduledWeek()).isEqualTo(Week.EVEN);
		assertThat(firstResult.getGarbageScheduledDay()).isEqualTo(WeekDay.TUESDAY);
		assertThat(firstResult.getFacilityCategory()).isEqualTo(FacilityCategory.VILLA);
		assertThat(firstResult.getAddress().getStreet()).isEqualTo(request.getStreet());
		assertThat(firstResult.getAddress().getPostalCode()).isEqualTo(request.getPostalCode());
		assertThat(firstResult.getAddress().getHouseNumber()).isEqualTo(request.getHouseNumber());
		assertThat(firstResult.getAddress().getCity()).isEqualTo(request.getCity());

		verify(service).getGarbageSchedules(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void updateGarbageSchedules() {
		webTestClient.put()
			.uri("/schedules")
			.exchange()
			.expectStatus()
			.isAccepted()
			.expectBody().isEmpty();

		verify(service).updateGarbageSchedulesAsynchronously();
		verifyNoMoreInteractions(service);
	}
}
