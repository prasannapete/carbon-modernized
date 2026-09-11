package com.pcpl.carbon.pcplsdk.Generic.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PCPLCRUDService<E, D,R extends JpaRepository<E,Long> > {
    public D save(D dto);

    List<D> saveAll(List<D> dtos);

    public ApplicationResponse getAll();
}
