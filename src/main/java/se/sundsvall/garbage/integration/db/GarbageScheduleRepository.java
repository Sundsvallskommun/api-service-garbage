package se.sundsvall.garbage.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;

public interface GarbageScheduleRepository extends JpaRepository<GarbageScheduleEntity, Long>, JpaSpecificationExecutor<GarbageScheduleEntity> {
}
