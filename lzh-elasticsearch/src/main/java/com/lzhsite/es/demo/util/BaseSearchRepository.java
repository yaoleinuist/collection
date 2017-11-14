package com.lzhsite.es.demo.util;

import java.io.Serializable;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BaseSearchRepository <E, ID extends Serializable> extends ElasticsearchRepository<E, ID>, PagingAndSortingRepository<E, ID>{
 

}
