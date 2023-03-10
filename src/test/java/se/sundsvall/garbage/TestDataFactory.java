package se.sundsvall.garbage;

import java.util.List;

import se.sundsvall.garbage.api.model.Address;
import se.sundsvall.garbage.api.model.FacilityCategory;
import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.api.model.GarbageScheduleResponse;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

public final class TestDataFactory {
    
    
    public static GarbageScheduleEntity buildGarbageScheduleEntity() {
        return GarbageScheduleEntity.builder()
            .withDriveSchedule("KP10FR1")
            .withId(1L)
            .withStreet("SomeStreet")
            .withCity("someCity")
            .withHouseNumber("2")
            .withPostalCode("12345")
            .withAdditionalInformation("A")
            .withFacilityCategory(FacilityCategory.VILLA)
            .build();
    }
    
    public static GarbageScheduleRequest buildGarbageScheduleRequest() {
        var request = GarbageScheduleRequest.builder()
            .withStreet("SomeStreet")
            .withHouseNumber("2")
            .withPostalCode("12345")
            .withCity("someCity")
            .withAdditionalInformation("A")
            .build();
    
        request.setPage(1);
        request.setLimit(10);
        return request;
    }
    
    public static List<GarbageScheduleResponse> buildGarbageScheduleResponse() {
        return List.of(GarbageScheduleResponse.builder()
            .withAddress(Address.builder().withStreet("SomeStreet").withCity("someCity").withHouseNumber("2").withPostalCode("12345").build())
            .withGarbageScheduledWeek(GarbageScheduleResponse.Week.EVEN)
            .withGarbageScheduledDay(GarbageScheduleResponse.WeekDay.TUESDAY)
            .withFacilityCategory(FacilityCategory.VILLA)
            .build());
    }
    
    private static Address buildAddress() {
        return Address.builder()
            .withStreet("SomeStreet")
            .withHouseNumber("2")
            .withPostalCode("12345")
            .build();
    }
}
 
