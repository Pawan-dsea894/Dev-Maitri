package com.iexceed.appzillonbanking.logs.repository.apz;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.logs.domain.apz.TbAslgTxnDetail;

@Repository
public interface TbAslgTxnDetailRepository extends CrudRepository<TbAslgTxnDetail,String>{

}
