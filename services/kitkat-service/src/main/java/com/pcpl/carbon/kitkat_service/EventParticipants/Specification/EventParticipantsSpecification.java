package com.pcpl.carbon.kitkat_service.EventParticipants.Specification;

import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;
import org.springframework.data.jpa.domain.Specification;

public class EventParticipantsSpecification {
    public static Specification<EventParticipants> textInAllColumns(String searchText) {
        if (!searchText.contains("%")) {
            searchText = "%" + searchText + "%";
        }
        String finalText = searchText;
        return (root, query, builder) -> builder.and(builder.or(
                        builder.like(root.get("name"), finalText)
                ),
                builder.equal(root.get("isDeleted"), 0)
        );
    }
}
