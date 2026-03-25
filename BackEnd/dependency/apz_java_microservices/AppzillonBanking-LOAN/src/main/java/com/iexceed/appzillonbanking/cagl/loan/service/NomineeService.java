package com.iexceed.appzillonbanking.cagl.loan.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iexceed.appzillonbanking.cagl.loan.payload.CustomerApplicationDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.NomineeDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class NomineeService {
	
	private static final Logger logger = LogManager.getLogger(NomineeService.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public JSONObject updateNomineeDetails(CustomerApplicationDtls nomineeDtls,
	                                       String applicationId,
	                                       String versionNo) throws Exception {

	    logger.debug("Printing update nomineeDtls :{} ", nomineeDtls);
	    logger.debug("Printing update applicationId :{} ", applicationId);
	    logger.debug("Printing update versionNo :{} ", versionNo);

	    int ver = Integer.parseInt(versionNo) + 1;
	    String verNo = String.valueOf(ver);

	    NomineeDetails nominee = nomineeDtls.getNomineeDetails();

	    ObjectMapper mapper = new ObjectMapper();

	    ObjectNode inputNode = mapper.createObjectNode();
	    inputNode.put("name", nominee.getInputData().getName());
	    inputNode.put("dob", nominee.getInputData().getDob());
	    inputNode.put("memRelation", nominee.getMemRelation());
	    inputNode.put("legaldocName", nominee.getLegaldocName());
	    inputNode.put("legaldocId", nominee.getLegaldocId());
	    inputNode.put("mobileNum", nominee.getInputData().getMobileNum());
	    inputNode.put("gender", nominee.getInputData().getGender());

	    String updatedInputData = mapper.writeValueAsString(inputNode);

	    ObjectNode ocrNode = mapper.createObjectNode();
	    ocrNode.put("name", nominee.getInputData().getName());
	    ocrNode.put("dob", nominee.getInputData().getDob());
	    ocrNode.put("memRelation", nominee.getMemRelation());
	    ocrNode.put("legaldocName", nominee.getLegaldocName());
	    ocrNode.put("legaldocId", nominee.getLegaldocId());
	    ocrNode.put("mobileNum", nominee.getInputData().getMobileNum());
	    ocrNode.put("gender", nominee.getInputData().getGender());

	    String updatedOcrDetails = mapper.writeValueAsString(ocrNode);

	    String nomineeDtlsJson = mapper.writeValueAsString(nominee);
	    String reasonJson = mapper.writeValueAsString(nominee.getReason());

	    String sql = """
	        UPDATE public.tb_ucno_customer_nominee_details
	        SET latest_version_no = :versionNum,
	            memRelation = :memRelation,
	            legaldocName = :legaldocName,
	            legaldocId = :legaldocId,
	            customer_name = :name,
	            InputData = :inputData,
	            ocrdetails = :ocrdetails,
	            nomineeDtls = :nomineeDtls,
	            reason = :reason,
	            docuNoF = :docuNoF,
	            docuNoB = :docuNoB
	        WHERE application_id = :applicationId
	        AND latest_version_no = :versionNo
	        """;
	    Query query = entityManager.createNativeQuery(sql);
	    query.setParameter("versionNum", verNo);
	    query.setParameter("memRelation", nominee.getMemRelation());
	    query.setParameter("legaldocName", nominee.getLegaldocName());
	    query.setParameter("legaldocId", nominee.getLegaldocId());
	    query.setParameter("name", nominee.getInputData().getName());
	    query.setParameter("inputData", updatedInputData);
	    query.setParameter("ocrdetails", updatedOcrDetails);
	    query.setParameter("nomineeDtls", nomineeDtlsJson);
	    query.setParameter("reason", reasonJson);
	    query.setParameter("docuNoF", nominee.getDocuNoF());
	    query.setParameter("docuNoB", nominee.getDocuNoB());
	    query.setParameter("applicationId", applicationId);
	    query.setParameter("versionNo", versionNo);
	    int updated = query.executeUpdate();
	    logger.debug("Printing updated :{} ", updated);
	    JSONObject json = new JSONObject();
	    json.put("versionNo", verNo);
	    json.put("rowsUpdated", updated);
	    return json;
	}

}
