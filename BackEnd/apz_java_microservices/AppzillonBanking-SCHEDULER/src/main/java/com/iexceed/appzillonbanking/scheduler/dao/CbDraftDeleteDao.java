package com.iexceed.appzillonbanking.scheduler.dao;

import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMaster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

public class CbDraftDeleteDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${CBCHECK_DRAFTDELETE_QUERY}")
    private String draftDeleteQuery;

    @SuppressWarnings("unchecked")
    public Optional<List<ApplicationMaster> > findDraftApplications() {
        return Optional.ofNullable(entityManager
                .createNativeQuery(draftDeleteQuery, ApplicationMaster.class)
                .getResultList());
    }
}
