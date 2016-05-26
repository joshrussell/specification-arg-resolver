package net.kaczmarzyk.spring.data.jpa.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class CollectionSpecificationGenerator<P, C> {

    final Class<P> parentClass;

    final String collectionPropertyName;

    final Class<C> collectionClass;

    public CollectionSpecificationGenerator(Class<P> parentClass, String collectionPropertyName, Class<C> collectionClass) {
        this.parentClass = parentClass;
        this.collectionPropertyName = collectionPropertyName;
        this.collectionClass = collectionClass;
    }

    @SuppressWarnings("unchecked")
    private Path getCollectionParentPath(Path root, String path) {
        List<String> pathElements = new LinkedList<>(Arrays.asList(path.split("\\.")));
        String collectionPath = pathElements.get(pathElements.size() - 1);
        pathElements.remove(pathElements.size() - 1);
        Path<?> retVal = root;
        for (String pathEl : pathElements) {
            retVal = (Path) retVal.get(pathEl);
        }
        return retVal.get(collectionPath);
    }

    public Specification equal(final String propertyName, final String value) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
//                Root<P> parent = root;
                Root<C> collection = query.from(collectionClass);
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.equal(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification notEqual(final String propertyName, final String value) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
//                Root<P> parent = root;
                Root<C> collection = query.from(collectionClass);
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.notEqual(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
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
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.like(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification in(final String propertyName, final List<String> value) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<P> parent = root;
                Root<C> collection = query.from(collectionClass);
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(getCollectionParentPath(collection, propertyName).in(value), cb.isMember(collection, collectionExpression));
            }
        };
    }
}
