package com.lzhsite.es.demo.util;

import java.io.Serializable;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseSearchService <E,ID extends Serializable,R extends BaseSearchRepository<E,ID>>{ //spring 4.X 支持泛型注入

    private Logger log = Logger.getLogger(this.getClass());

    private R repository;

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    protected R getRepository(){
        return repository;
    }

    public E getById(ID id) {//
        return getRepository().findOne(id);
    }

    public Iterable<E> listAll() {
        return getRepository().findAll();
    }

    public void save(E data){
        getRepository().save(data);
    }

    public void delete(E data){
        getRepository().delete(data);
    }

    public void deleteById(ID id){
        getRepository().delete(id);
    }

    public E getByKey(String fieldName, Object value){
        try{
            return getRepository().search(QueryBuilders.matchQuery(fieldName, value)).iterator().next();
        }catch(NoSuchElementException e){
            return null;
        }
    }

}
