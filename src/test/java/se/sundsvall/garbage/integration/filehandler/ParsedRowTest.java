package se.sundsvall.garbage.integration.filehandler;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class ParsedRowTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ParsedRow.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testGettersAndSetters() {

		// Arrange
		final var row = new ParsedRow();
		final var address = "Testgatan";
		final var houseNumber = "12";
		final var postalCode = "85731";
		final var city = "Sundsvall";
		final var additionalInformation = "A";
		final var facilityCategory = "VILLA";
		final var driveSchedule = "KM021";

		// Act
		row.setAddress(address);
		row.setAdressNumber(houseNumber);
		row.setAdditionalInformation(additionalInformation);
		row.setFacilityCategory(facilityCategory);
		row.setDriveSchedule(driveSchedule);
		row.setZipcode(postalCode);
		row.setCity(city);

		// Assert
		assertThat(row).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(row.getAddress()).isEqualTo(address);
		assertThat(row.getZipcode()).isEqualTo(postalCode);
		assertThat(row.getCity()).isEqualTo(city);
		assertThat(row.getAdditionalInformation()).isEqualTo(additionalInformation);
		assertThat(row.getAdressNumber()).isEqualTo(houseNumber);
		assertThat(row.getFacilityCategory()).isEqualTo(facilityCategory);
		assertThat(row.getDriveSchedule()).isEqualTo(driveSchedule);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new ParsedRow()).hasAllNullFieldsOrProperties();
	}

}
