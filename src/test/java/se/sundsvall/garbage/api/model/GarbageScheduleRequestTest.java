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

class GarbageScheduleRequestTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(GarbageScheduleRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		// Arrange
		final var street = "Testgatan";
		final var houseNumber = "12";
		final var postalCode = "85731";
		final var city = "Sundsvall";
		final var additionalInformation = "A";
		final var page = 1;
		final var limit = 10;

		// Act
		final var request = GarbageScheduleRequest.builder()
			.withStreet(street)
			.withHouseNumber(houseNumber)
			.withPostalCode(postalCode)
			.withCity(city)
			.withAdditionalInformation(additionalInformation)
			.withPage(page)
			.withLimit(limit)
			.build();

		// Assert
		assertThat(request.getPage()).isEqualTo(page);
		assertThat(request.getLimit()).isEqualTo(limit);
		assertThat(request.getStreet()).isEqualTo(street);
		assertThat(request.getPostalCode()).isEqualTo(postalCode);
		assertThat(request.getCity()).isEqualTo(city);
		assertThat(request.getAdditionalInformation()).isEqualTo(additionalInformation);
		assertThat(request.getHouseNumber()).isEqualTo(houseNumber);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(GarbageScheduleRequest.builder().build()).hasAllNullFieldsOrPropertiesExcept("page", "limit");
		assertThat(new GarbageScheduleRequest()).hasAllNullFieldsOrPropertiesExcept("page", "limit");
	}

}
