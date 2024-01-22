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

class AddressTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Address.class, allOf(
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

		// Act
		final var address = Address.builder()
			.withStreet(street)
			.withHouseNumber(houseNumber)
			.withPostalCode(postalCode)
			.withCity(city)
			.build();

		// Assert
		assertThat(address).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(address.getStreet()).isEqualTo(street);
		assertThat(address.getHouseNumber()).isEqualTo(houseNumber);
		assertThat(address.getPostalCode()).isEqualTo(postalCode);
		assertThat(address.getCity()).isEqualTo(city);

	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Address.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new Address()).hasAllNullFieldsOrProperties();
	}

}
