package mujina.idp.service;

import java.util.List;

public interface CrudRepository<T, ID> {
    ID add(T t);

    void delete(ID id);

    T update(T t);

    T findOne(ID id);

    List<T> findAll();
}
