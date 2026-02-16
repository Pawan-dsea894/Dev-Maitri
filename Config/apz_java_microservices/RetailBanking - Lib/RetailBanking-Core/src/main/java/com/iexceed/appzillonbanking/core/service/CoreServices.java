package com.iexceed.appzillonbanking.core.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiCommonCodeDomain;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiCommonCodeId;
import com.iexceed.appzillonbanking.core.repository.ab.TbAbmiCommonCodeRepository;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Service
public class CoreServices {

	private static final Logger logger = LogManager.getLogger(CoreServices.class);

	@Autowired
	private TbAbmiCommonCodeRepository tbAbmiCommonCodeRepository;

	// private static final String PRIMARY_CCY = "PRIMARYCURRENCY";
	// private static final String ACCOUNT_MASK_CODE = "ACCOUNTMASK";
	private static final String ACCOUNT_MASK_CODE = "MASKING";
	private static final String ACCOUNT_MASK = "AccountMask";
	// private static final String MASKING = "DashboardMasking";
	private static final String COMM = "COMM";
	private static final String MASKING = "AccountMasking";
	private static final String ENCRYPTION = "Encryption";
	private static final String FLAG = "flag";
	private static final String ACCOUNT_MASKING_DETAILS = "Account Masking details = {}";

	/**
	 * @return Function to get the Primary currency data from Common Code.
	 * @author akshay.upadhya
	 */
	public String getPrimaryCurrency() {
		String primaryCcy = "";
		Optional<TbAbmiCommonCodeDomain> tbCommonCode = tbAbmiCommonCodeRepository
				.findByCodeAndCodeType(CommonUtils.getExternalProperties("primaryCurrencyNode"), COMM);
		// primaryCcy = CommonUtils.getExternalProperties(CommonUtils.PRIMARY_CCY);
		if (tbCommonCode.isPresent()) {
			primaryCcy = tbCommonCode.get().getCodeDesc();
		}
		return primaryCcy;
	}

	public String getMaskingFlag(TbAbmiCommonCodeDomain tbCommonCode) {
		logger.debug("Start: getMaskingFlag");
		String maskingFlag = null;

		try {
			logger.debug(ACCOUNT_MASKING_DETAILS, tbCommonCode);
			if (null != tbCommonCode) {
				JSONObject maskingDetails = new JSONObject(tbCommonCode.getCodeDesc());
				maskingFlag = maskingDetails.getJSONObject(ACCOUNT_MASK).getJSONObject(MASKING).get(FLAG).toString();
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching masking details = " + e);
		}

		logger.debug("End: getMaskingFlag with response = " + maskingFlag);
		return maskingFlag;
	}

	public String getMaskingCharacter(TbAbmiCommonCodeDomain tbCommonCode) {
		logger.debug("Start: getMaskingCharacter");
		String maskingFlag = null;

		try {
			logger.debug(ACCOUNT_MASKING_DETAILS, tbCommonCode);

			if (null != tbCommonCode) {
				JSONObject maskingDetails = new JSONObject(tbCommonCode.getCodeDesc());
				maskingFlag = maskingDetails.getJSONObject(ACCOUNT_MASK).getJSONObject(MASKING).get("maskCharacter")
						.toString();
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching masking character = " + e);
		}

		logger.debug("End: getMaskingCharacter with response = " + maskingFlag);
		return maskingFlag;
	}

	public int getMaskingStartPosition(TbAbmiCommonCodeDomain tbCommonCode) {
		logger.debug("Start: getMaskingStartPosition");
		String maskingStartPosition = null;

		try {
			logger.debug(ACCOUNT_MASKING_DETAILS, tbCommonCode);

			if (null != tbCommonCode) {
				JSONObject maskingDetails = new JSONObject(tbCommonCode.getCodeDesc());
				maskingStartPosition = maskingDetails.getJSONObject(ACCOUNT_MASK).getJSONObject(MASKING)
						.get("StartPosition").toString();
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching masking start position = " + e);
		}

		logger.debug("End: getMaskingStartPosition with response = " + maskingStartPosition);
		return Integer.parseInt(maskingStartPosition);
	}

	public int getMaskingCharCount(TbAbmiCommonCodeDomain tbCommonCode) {
		logger.debug("Start: getMaskingCharCount");
		String maskingCharCount = null;

		try {
			logger.debug(ACCOUNT_MASKING_DETAILS + tbCommonCode);

			if (null != tbCommonCode) {
				JSONObject maskingDetails = new JSONObject(tbCommonCode.getCodeDesc());
				maskingCharCount = maskingDetails.getJSONObject(ACCOUNT_MASK).getJSONObject(MASKING)
						.get("TotalMaskCount").toString();
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching masking character count = " + e);
		}

		logger.debug("End: getMaskingCharCount with response = " + maskingCharCount);
		return Integer.parseInt(maskingCharCount);
	}

	public String getEncryptionFlag() {
		logger.debug("Start: getEncryptionFlag");
		String encryptionFlag = null;

		try {
			Optional<TbAbmiCommonCodeDomain> tbCommonCode = tbAbmiCommonCodeRepository.findByCodeAndCodeType(ACCOUNT_MASK_CODE, COMM);

			logger.debug("Account Encryption details = " + tbCommonCode);

			if (tbCommonCode.isPresent()) {
				JSONObject encryptionDetails = new JSONObject(tbCommonCode.get().getCodeDesc());
				encryptionFlag = encryptionDetails.getJSONObject(ACCOUNT_MASK).getJSONObject(ENCRYPTION).get(FLAG)
						.toString();
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching encryption details = " + e);
		}

		logger.debug("End: getEncryptionFlag with response = " + encryptionFlag);
		return encryptionFlag;
	}

	public TbAbmiCommonCodeDomain getCommonCodeMaskingObj() {
		logger.debug("Start: getCommonCodeMaskingObj");
		TbAbmiCommonCodeDomain tbCommonCode = null;

		try {
			Optional<TbAbmiCommonCodeDomain> tbCommonCodeOpt = tbAbmiCommonCodeRepository
					.findByCodeAndCodeType(ACCOUNT_MASK_CODE, COMM);
			if (tbCommonCodeOpt.isPresent()) {
				tbCommonCode = tbCommonCodeOpt.get();
				logger.debug(ACCOUNT_MASKING_DETAILS, tbCommonCode);
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching masking details = " + e);
		}

		logger.debug("End: getCommonCodeMaskingObj with response = " + tbCommonCode);
		return tbCommonCode;
	}
}
