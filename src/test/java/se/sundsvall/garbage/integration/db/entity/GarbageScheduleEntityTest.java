package se.sundsvall.garbage.integration.db.entity;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import se.sundsvall.garbage.api.model.enums.FacilityCategory;

class GarbageScheduleEntityTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(GarbageScheduleEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		// Arrange
		final var id = 1L;
		final var street = "Testgatan";
		final var houseNumber = "12";
		final var postalCode = "85731";
		final var city = "Sundsvall";
		final var additionalInformation = "A";
		final var facilityCategory = FacilityCategory.VILLA;
		final var driveSchedule = "KM021";
		
		// Act
		final var request = GarbageScheduleEntity.builder()
			.withId(id)
			.withStreet(street)
			.withHouseNumber(houseNumber)
			.withPostalCode(postalCode)
			.withCity(city)
			.withAdditionalInformation(additionalInformation)
			.withFacilityCategory(facilityCategory)
			.withDriveSchedule(driveSchedule)
			.build();

		// Assert
		assertThat(request).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(request.getId()).isEqualTo(id);
		assertThat(request.getStreet()).isEqualTo(street);
		assertThat(request.getPostalCode()).isEqualTo(postalCode);
		assertThat(request.getCity()).isEqualTo(city);
		assertThat(request.getAdditionalInformation()).isEqualTo(additionalInformation);
		assertThat(request.getHouseNumber()).isEqualTo(houseNumber);
		assertThat(request.getFacilityCategory()).isEqualTo(facilityCategory);
		assertThat(request.getDriveSchedule()).isEqualTo(driveSchedule);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(GarbageScheduleEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new GarbageScheduleEntity()).hasAllNullFieldsOrProperties();
	}


}
