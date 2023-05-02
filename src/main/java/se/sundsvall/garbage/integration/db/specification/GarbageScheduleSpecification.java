package se.sundsvall.garbage.integration.db.specification;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import se.sundsvall.garbage.api.model.GarbageScheduleRequest;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity;
import se.sundsvall.garbage.integration.db.entity.GarbageScheduleEntity_;

import jakarta.persistence.criteria.Predicate;

@Component
public class GarbageScheduleSpecification {
    
    public Specification<GarbageScheduleEntity> createGarbageScheduleSpecification(GarbageScheduleRequest request) {
        
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            
            if (StringUtils.isNotBlank(request.getAdditionalInformation())) {
                predicates.add(criteriaBuilder.equal(root.get(GarbageScheduleEntity_.ADDITIONAL_INFORMATION),
                    request.getAdditionalInformation()));
            }
            if (StringUtils.isNotBlank(request.getStreet())) {
                predicates.add(criteriaBuilder.equal(root.get(GarbageScheduleEntity_.STREET),
                    request.getStreet()));
            }
            if (StringUtils.isNotBlank(request.getHouseNumber())) {
                predicates.add(criteriaBuilder.equal(root.get(GarbageScheduleEntity_.HOUSE_NUMBER),
                    request.getHouseNumber()));
            }
            if (StringUtils.isNotBlank(request.getPostalCode())) {
                predicates.add(criteriaBuilder.equal(root.get(GarbageScheduleEntity_.POSTAL_CODE), request.getPostalCode()));
            }
            if (StringUtils.isNotBlank(request.getCity())) {
                predicates.add(criteriaBuilder.equal(root.get(GarbageScheduleEntity_.CITY), request.getCity()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
