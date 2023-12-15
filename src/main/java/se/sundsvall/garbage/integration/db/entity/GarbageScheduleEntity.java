package se.sundsvall.garbage.integration.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import se.sundsvall.garbage.api.model.FacilityCategory;

@Entity
@Table(name = "garbageschedule")
@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class GarbageScheduleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "street")
	private String street;

	@Column(name = "houseNumber")
	private String houseNumber;

	@Column(name = "postalCode")
	private String postalCode;

	@Column(name = "city")
	private String city;

	@Column(name = "additionalInformation")
	private String additionalInformation;

	@Column(name = "facilityCategory")
	private FacilityCategory facilityCategory;

	@Column(name = "driveSchedule")
	private String driveSchedule;

}
