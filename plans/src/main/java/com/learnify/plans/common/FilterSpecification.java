package com.learnify.plans.common;

import com.learnify.plans.plans.domain.Plan;
import org.springframework.data.jpa.domain.Specification;

public class FilterSpecification {

    /**
     * Creates a JPA Specification with a case-insensitive "contains" filter for a given field name.
     * <p>
     * This method generates a SQL "LIKE" clause to perform a partial match on the specified field,
     * making it useful for filtering entities by a substring match. If the `value` is null or empty,
     * it returns a conjunction (no-op) predicate, which has no effect on the query.
     * </p>
     *
     * @param queryName the name of the field in the entity to be filtered
     * @param value the substring value to match in the specified field, case-insensitively
     * @return Specification<Plan> a JPA Specification with a "contains" filter if `value` is not null or empty, or a no-op otherwise
     *
     * @implNote This method is designed to work with JPA criteria queries in Spring Data JPA repositories.
     * When applied, it will use a SQL "LIKE" clause to filter results based on the specified field and value.
     *
     * @see Specification
     */
    public static <T>Specification<T> contains(String queryName, String value) {
        return (root, query, builder) ->
            value != null && !value.isEmpty() ?
                builder.like(
                        builder.lower(root.get(queryName)),
                        "%" + value.toLowerCase() +"%"
                )
            : builder.conjunction();
    }
}
