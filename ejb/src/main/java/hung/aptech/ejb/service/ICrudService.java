package hung.aptech.ejb.service;

import hung.aptech.ejb.entity.BaseEntity;
import model.Query;

import java.util.List;
import java.util.Optional;

public interface ICrudService<T extends BaseEntity> {
    List<T> findAll(Query query);
    Optional<T> findById(Long id);
    Optional<T> save(T entity);
    List<T> saveAll(Iterable<T> entities);
    void deleteById(Long id);
    void deleteAll(Iterable<T> entities);
    long count();
    boolean existsById(Long id);
    List<T> findAllById(Iterable<Long> ids);
    Optional<T> findOne(Query query);
    Optional<T> update(T entity);
}
