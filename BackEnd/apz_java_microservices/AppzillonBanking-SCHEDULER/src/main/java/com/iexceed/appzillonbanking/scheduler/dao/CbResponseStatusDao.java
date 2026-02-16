package com.iexceed.appzillonbanking.scheduler.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.scheduler.model.CbResponseStatus;
import com.iexceed.appzillonbanking.scheduler.service.CancelScheduler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class CbResponseStatusDao {

	@PersistenceContext
	EntityManager em;

	@Value("${CBCHECK_FAILURE_QUERY}")
	private String failureQuery;
	
	@Value("${CBCHECK_AFTER_INTERVAL_QUERY}")
	private String afterIntervalQuery;
	
	private static final Logger logger = LogManager.getLogger(CbResponseStatusDao.class);

	public List<CbResponseStatus> fetchCbCheckFailureApplications(Integer maxRetryCount) {
		List<CbResponseStatus> list = new ArrayList<CbResponseStatus>();
		Query q = em.createNativeQuery(failureQuery);
		q.setParameter("retryCount", maxRetryCount);
		List<Object[]> objList = q.getResultList();
		for (Object[] obj : objList) {
			CbResponseStatus resp = new CbResponseStatus();
			resp.setApplicationId((String) obj[0]);
			resp.setVersionNo((String) obj[1]);
			resp.setUserId((String) obj[2]);
			list.add(resp);
		}

		return list;
	}
	
	public List<CbResponseStatus> fetchCbCheckAfterSomeDaysApplications() {
		List<CbResponseStatus> list = new ArrayList<CbResponseStatus>();
		Query q = em.createNativeQuery(afterIntervalQuery);
		List<Object[]> objList = q.getResultList();
		for (Object[] obj : objList) {
			CbResponseStatus resp = new CbResponseStatus();
			resp.setApplicationId((String) obj[1]);
			resp.setVersionNo((String) obj[2]);
			resp.setMemberId((String) obj[4]);
			resp.setUserId((String) obj[7]);
			list.add(resp);
		}

		return list;
	}
	
	@Transactional
	public void updateCBCheckStatus(String applicationId) {
		try {
			Query q = em.createNativeQuery("update tb_uaob_cb_response set status='CANCELLED', cb_check_status='CANCELLED' where application_id=:applicationId");
			q.setParameter("applicationId", applicationId);
			 logger.debug("Executing query: update tb_uaob_cb_response set status='CANCELLED', cb_check_status='CANCELLED' where application_id=:applicationId with parameter applicationId: {}", applicationId);
		     logger.debug("Query object: {}, Parameter applicationId: {}", q, applicationId);
			q.executeUpdate();
		}catch(Exception exp) {
			logger.error("Error Occurred while updating CB Check status : {}", exp);
		}
	}
	

	@Transactional
	public void updateCBCheckStatusBulk(List<String> applicationIds) {
	    try {
	        if (applicationIds == null || applicationIds.isEmpty()) {
	            return;
	        }

	        Query q = em.createNativeQuery(
	            "UPDATE tb_uaob_cb_response " +
	            "SET status='CANCELLED', cb_check_status='CANCELLED' " +
	            "WHERE application_id IN (:applicationIds)"
	        );
	        q.setParameter("applicationIds", applicationIds);

	        logger.debug("Executing bulk update for applicationIds: {}", applicationIds);
	        q.executeUpdate();

	    } catch (Exception exp) {
	        logger.error("Error occurred while bulk updating CB check statuses: {}", exp.getMessage(), exp);
	    }
	}

	
}
