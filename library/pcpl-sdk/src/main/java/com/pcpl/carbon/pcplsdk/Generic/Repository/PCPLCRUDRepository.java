package com.pcpl.carbon.pcplsdk.Generic.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PCPLCRUDRepository<E> extends JpaRepository<E, Long> {
    public Page<E> findAll(Specification<E> specification, Pageable page);
    public E getById(Long id);
    public List<E> findAll();

}
