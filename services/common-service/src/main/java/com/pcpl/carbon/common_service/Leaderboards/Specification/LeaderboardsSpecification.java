package com.pcpl.carbon.common_service.Leaderboards.Specification;

import com.pcpl.carbon.pcplsdk.Leaderboards.Model.Leaderboards;
import org.springframework.data.jpa.domain.Specification;

public class LeaderboardsSpecification {
    public static Specification<Leaderboards> textInAllColumns(String searchText) {

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
