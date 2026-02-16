package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationMasterHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationMasterHisId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationMasterHisRepository extends CrudRepository<ApplicationMasterHis, ApplicationMasterHisId> {

    List<ApplicationMasterHis> findByApplicationId(String applicationId);

}
