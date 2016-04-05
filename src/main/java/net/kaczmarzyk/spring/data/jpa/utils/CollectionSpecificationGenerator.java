package net.kaczmarzyk.spring.data.jpa.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Collection;

public class CollectionSpecificationGenerator<P, C> {

    final Class<P> parentClass;

    final String collectionPropertyName;

    final Class<C> collectionClass;

    public CollectionSpecificationGenerator(Class<P> parentClass, String collectionPropertyName, Class<C> collectionClass) {
        this.parentClass = parentClass;
        this.collectionPropertyName = collectionPropertyName;
        this.collectionClass = collectionClass;
    }

    public Specification equal(final String propertyName, final String value) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<P> parent = root;
                Root<C> collection = query.from(collectionClass);
                Expression<Collection<C>> collectionExpression = parent.get(collectionPropertyName);
                return cb.and(cb.equal(collection.get(propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification like(final String propertyName, final String value) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<P> parent = root;
                Root<C> collection = query.from(collectionClass);
                Expression<Collection<C>> collectionExpression = parent.get(collectionPropertyName);
                return cb.and(cb.like(collection.<String>get(propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }
}
