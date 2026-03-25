package se.sundsvall.garbage.api.model;

import java.time.LocalDate;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

class WasteScheduleTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(WasteSchedule.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		// Arrange
		final var wasteType = "WASTE";
		final var nextPickupDate = LocalDate.of(2026, 3, 31);

		// Act
		final var result = WasteSchedule.builder()
			.withWasteType(wasteType)
			.withNextPickupDate(nextPickupDate)
			.build();

		// Assert
		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getWasteType()).isEqualTo(wasteType);
		assertThat(result.getNextPickupDate()).isEqualTo(nextPickupDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(WasteSchedule.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new WasteSchedule()).hasAllNullFieldsOrProperties();
	}

}
