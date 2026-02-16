package com.iexceed.appzillonbanking.scheduler.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.scheduler.domain.ab.T24ServerDate;

@Repository
public interface T24ServerDateRepository extends CrudRepository<T24ServerDate, Integer>{

}
