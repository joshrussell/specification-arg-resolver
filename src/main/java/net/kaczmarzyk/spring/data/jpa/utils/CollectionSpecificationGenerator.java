package net.kaczmarzyk.spring.data.jpa.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionSpecificationGenerator<P, C> {

    private String collectionPropertyName;

    public CollectionSpecificationGenerator() {
    }

    public CollectionSpecificationGenerator(String collectionPropertyName) {
        this.collectionPropertyName = collectionPropertyName;
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

    public Specification<P> equal(final String collectionClassString, final String propertyName, final String value) {
        return new Specification<P>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<C> collection = null;
                try {
                    collection = query.from(Class.forName(collectionClassString));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.equal(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification<P> notEqual(final String collectionClassString, final String propertyName, final String value) {
        return new Specification<P>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<C> collection = null;
                try {
                    collection = query.from(Class.forName(collectionClassString));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.notEqual(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification<P> like(final String collectionClassString, final String propertyName, final String value) {
        return new Specification<P>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<C> collection = null;
                try {
                    collection = query.from(Class.forName(collectionClassString));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(cb.like(getCollectionParentPath(collection, propertyName), value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public Specification<P> in(final String collectionClassString, final String propertyName, final List<String> value) {
        return new Specification<P>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                query.distinct(true);
                Root<C> collection = null;
                try {
                    collection = query.from(Class.forName(collectionClassString));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Expression<Collection<C>> collectionExpression = getCollectionParentPath(root, collectionPropertyName);
                return cb.and(getCollectionParentPath(collection, propertyName).in(value), cb.isMember(collection, collectionExpression));
            }
        };
    }

    public String getCollectionPropertyName() {
        return collectionPropertyName;
    }

    public void setCollectionPropertyName(String collectionPropertyName) {
        this.collectionPropertyName = collectionPropertyName;
    }
}
