package com.pcpl.carbon.pcplsdk.Generic.Controller;

import com.pcpl.carbon.pcplsdk.Common.Request.ApplicationRequest;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Component
public abstract class AbstractCRUDController<E, D, R extends PCPLCRUDRepository<E>, S extends AbstractLazyService<E, D, R>>{
    @Autowired
    S service;

    @PostMapping("/save")
    public ResponseEntity<ApplicationResponse> save(@RequestBody D dto){
        return ResponseEntity.ok(ApplicationResponse.builder().success(true).data(service.save(dto)).build());
    }

    @PostMapping("/save-multiple")
    public ResponseEntity<ApplicationResponse> saveMultiple(@RequestBody List<D> dto){
        return ResponseEntity.ok(ApplicationResponse.builder().success(true).data(service.saveAll(dto)).build());
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApplicationResponse> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/all")
    public ResponseEntity<ApplicationResponse> getAll(@RequestBody ApplicationRequest request){
        return ResponseEntity.ok(service.getAll(request));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApplicationResponse moveToTrash(@PathVariable Long id) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse response = service.moveToTrash(id);

        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseEntity.ok(response);
        return response;
    }

    @DeleteMapping(value = "/{id}")
    public ApplicationResponse deleteById(@PathVariable Long id) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse response = service.deleteById(id);

        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseEntity.ok(response);
        return response;
    }
}
