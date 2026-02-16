package com.iexceed.appzillonbanking.logs.repository.apz;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.logs.domain.apz.TbAstpLdRecs;
import com.iexceed.appzillonbanking.logs.domain.apz.TbAstpLdRecsPK;

@Repository
public interface TbAstpLdRecsRepository extends CrudRepository<TbAstpLdRecs, TbAstpLdRecsPK>{

}
