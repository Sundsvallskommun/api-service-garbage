package se.sundsvall.garbage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

class ShedlockAnnotationsTest {

	@Test
	void verifyMandatorySchedlockAnnotations() {
		final var scanner = new ClassPathScanningCandidateComponentProvider(true);
		final var candidates = scanner.findCandidateComponents(this.getClass().getPackageName());

		candidates.stream()
			.map(this::getMethodsAnnotatedWith)
			.flatMap(m -> m.entrySet().stream())
			.forEach(this::verifyAnnotations);
	}

	private void verifyAnnotations(final Entry<String, List<Method>> entrySet) {
		entrySet.getValue().forEach(method -> {
			final var annotation = method.getAnnotation(Dept44Scheduled.class);
			// Verify that @Dept44Scheduled has lockAtMostFor configured
			assertThat(annotation.lockAtMostFor())
				.withFailMessage(() -> "Method %s in class %s has @Dept44Scheduled annotation but no lockAtMostFor configured".formatted(method.getName(), entrySet.getKey()))
				.isNotBlank();
		});
	}

	private Map<String, List<Method>> getMethodsAnnotatedWith(final BeanDefinition candidate) {
		try {
			final List<Method> methods = new ArrayList<>();
			var klazz = Class.forName(candidate.getBeanClassName());
			while (klazz != Object.class) {
				// need to traverse a type hierarchy to process methods from super types iterate though the list of methods
				// declared in the class represented by klass variable, and add those annotated with the specified annotation
				for (final Method method : klazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Dept44Scheduled.class)) {
						methods.add(method);
					}
				}
				// move to the upper class in the hierarchy in search for more methods
				klazz = klazz.getSuperclass();
			}
			return Map.of(Objects.requireNonNull(candidate.getBeanClassName()), methods);
		} catch (final ClassNotFoundException _) {
			fail("Couldn't traverse class methods as class %s could not be found".formatted(candidate.getBeanClassName()));
			return Collections.emptyMap();
		}
	}
}
