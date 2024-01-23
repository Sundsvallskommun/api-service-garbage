package se.sundsvall.garbage.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import se.sundsvall.garbage.api.model.enums.Week;
import se.sundsvall.garbage.api.model.enums.WeekDay;

class GarbageScheduleResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(GarbageScheduleResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		// Arrange
		final var address = Address.builder().build();
		final var additionalInformation = "A";
		final var garbageScheduledDay = WeekDay.TUESDAY;
		final var garbageScheduledWeek = Week.EVEN;

		// Act
		final var response = GarbageScheduleResponse.builder()
			.withAddress(address)
			.withAdditionalInformation(additionalInformation)
			.withGarbageScheduledDay(garbageScheduledDay)
			.withGarbageScheduledWeek(garbageScheduledWeek)
			.build();

		// Assert
		assertThat(response.getGarbageScheduledWeek()).isEqualTo(garbageScheduledWeek);
		assertThat(response.getGarbageScheduledDay()).isEqualTo(garbageScheduledDay);
		assertThat(response.getAddress()).isEqualTo(address);
		assertThat(response.getAdditionalInformation()).isEqualTo(additionalInformation);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(GarbageScheduleResponse.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new GarbageScheduleResponse()).hasAllNullFieldsOrProperties();
	}

}
