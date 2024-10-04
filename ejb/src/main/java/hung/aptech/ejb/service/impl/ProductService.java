package hung.aptech.ejb.service.impl;

import hung.aptech.ejb.entity.Product;
import hung.aptech.ejb.service.ICrudService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import model.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
@Slf4j
public class ProductService implements ICrudService<Product> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findAll(Query query) {
        log.info("Executing findAll with query: {}", query);
        String jpql = "SELECT p FROM Product p";
        StringBuilder conditions = new StringBuilder();

        if (query.getFilterFields() != null && !query.getFilterFields().isEmpty()) {
            conditions.append(" WHERE ");
            for (int i = 0; i < query.getFilterFields().size(); i++) {
                Query.FilterField filter = query.getFilterFields().get(i);
                conditions.append("p.")
                        .append(filter.getField())
                        .append(" ")
                        .append(filter.getOperator().getValue())
                        .append(" :filter").append(i);
                if (i < query.getFilterFields().size() - 1) {
                    conditions.append(" AND ");
                }
            }
        }

        if (query.getSortFields() != null && !query.getSortFields().isEmpty()) {
            conditions.append(" ORDER BY ");
            for (int i = 0; i < query.getSortFields().size(); i++) {
                Query.SortField sortField = query.getSortFields().get(i);
                conditions.append("p.")
                        .append(sortField.getField())
                        .append(sortField.isAscending() ? " ASC" : " DESC");
                if (i < query.getSortFields().size() - 1) {
                    conditions.append(", ");
                }
            }
        }

        jpql += conditions.toString();
        TypedQuery<Product> typedQuery = entityManager.createQuery(jpql, Product.class);

        if (query.getFilterFields() != null) {
            for (int i = 0; i < query.getFilterFields().size(); i++) {
                Query.FilterField filter = query.getFilterFields().get(i);
                typedQuery.setParameter("filter" + i, filter.getValue());
            }
        }

        if (!query.isGetAllRecords()) {
            typedQuery.setFirstResult((query.getPageNumber() - 1) * query.getPageSize());
            typedQuery.setMaxResults(query.getPageSize());
        }

        List<Product> results = typedQuery.getResultList();
        log.info("Found {} products", results.size());
        return results;
    }

    @Override
    public Optional<Product> findById(Long id) {
        log.info("Finding product by id: {}", id);
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    @Override
    public Optional<Product> save(Product entity) {
        log.info("Saving product: {}", entity);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
            log.info("Product saved successfully: {}", entity);
            return Optional.of(entity);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error saving product: {}", entity, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Product> saveAll(Iterable<Product> entities) {
        log.info("Saving all products");
        List<Product> savedProducts = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (Product product : entities) {
                savedProducts.add(save(product).orElse(null));
            }
            transaction.commit();
            log.info("Saved {} products", savedProducts.size());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error saving products", e);
        }
        return savedProducts;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting product by id: {}", id);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            findById(id).ifPresent(entityManager::remove);
            transaction.commit();
            log.info("Product deleted successfully with id: {}", id);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error deleting product with id: {}", id, e);
        }
    }

    @Override
    public void deleteAll(Iterable<Product> entities) {
        log.info("Deleting all products");
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (Product product : entities) {
                deleteById(product.getId());
            }
            transaction.commit();
            log.info("Deleted {} products", entities.spliterator().getExactSizeIfKnown());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error deleting products", e);
        }
    }

    @Override
    public long count() {
        log.info("Counting products");
        String jpql = "SELECT COUNT(p) FROM Product p";
        long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        log.info("Total products count: {}", count);
        return count;
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Checking existence of product by id: {}", id);
        boolean exists = findById(id).isPresent();
        log.info("Product with id {} exists: {}", id, exists);
        return exists;
    }

    @Override
    public List<Product> findAllById(Iterable<Long> ids) {
        log.info("Finding all products by ids: {}", ids);
        List<Product> results = new ArrayList<>();
        for (Long id : ids) {
            findById(id).ifPresent(results::add);
        }
        log.info("Found {} products by ids", results.size());
        return results;
    }

    @Override
    public Optional<Product> findOne(Query query) {
        log.info("Finding one product with query: {}", query);
        List<Product> results = findAll(query);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public Optional<Product> update(Product entity) {
        log.info("Updating product: {}", entity);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (existsById(entity.getId())) {
                Product updatedProduct = entityManager.merge(entity);
                transaction.commit();
                log.info("Product updated successfully: {}", updatedProduct);
                return Optional.of(updatedProduct);
            }
            transaction.rollback();
            log.warn("Product not found for update: {}", entity.getId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error updating product: {}", entity, e);
        }
        return Optional.empty();
    }
}
