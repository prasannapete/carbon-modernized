package com.pcpl.carbon.kitkat_service.Events.Specification;


import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import org.springframework.data.jpa.domain.Specification;

public class EventsSpecification {
    public static Specification<Events> textInAllColumns(String searchText) {
        if (!searchText.contains("%")) {
            searchText = "%" + searchText + "%";
        }
        String finalText = searchText;
        return (root, query, builder) -> builder.and(builder.or(
                        builder.like(root.get("eventName"), finalText)
                ),
                builder.equal(root.get("isDeleted"), 0)
        );
    }
}
