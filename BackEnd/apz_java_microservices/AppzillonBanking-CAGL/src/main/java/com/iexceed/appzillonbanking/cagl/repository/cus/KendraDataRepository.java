package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.dto.CollectionDetailsDto;
import com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto;
import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;

public interface KendraDataRepository extends JpaRepository<GkKendraData, Integer> {

	@Query(value = ("select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kmid = :userId  and kendraId not in (:excludedkList) and KENDRA_STATUS=:kendraStatus AND BRANCHID =:branchId "
			+ "union "
			+ "	select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where (kmid = :userId  or kendraId in (:kList)) AND BRANCHID =:branchId  AND KENDRA_STATUS=:kendraStatus"), nativeQuery = true)
	public List<GkKendraData> findKendraList(String userId, String kendraStatus, List<String> kList, List<String> excludedkList,String branchId);
	
	@Query(value = ("select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kmid = :userId  and kendraId NOT IN (:excludedkList) and KENDRA_STATUS=:kendraStatus AND BRANCHID =:branchId "
			+ "union "
			+ "	select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kendraId IN (:kList) and KENDRA_STATUS=:kendraStatus"), nativeQuery = true)
	public List<GkKendraData> findListForAssignedAndExcludedKendra(String userId, String kendraStatus, List<String> kList, List<String> excludedkList,String branchId);

	/*(value = "SELECT kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID"
			+ " FROM gk_kendra_data WHERE kmid = :userId AND KENDRA_STATUS = :kendraStatus"
			+ " UNION"
			+ " SELECT kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID"
			+ " FROM gk_kendra_data WHERE kmid IN"
			+ " (SELECT kendra_id FROM tb_kendra_assignment WHERE kmid =:userId)"
			+ " AND KENDRA_STATUS =:kendraStatus", nativeQuery = true)
	public List<GkKendraData> findKendraList(@Param("userId") String userId, @Param("kendraStatus") String kendraStatus);*/

	public List<GkKendraData> findByBranchId(String userId);
	
//	@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.CollectionDetailsDto(k.kendraId, k.branchId,"
//			+ " k.kendraName, k.nextMeetingDate,k.meetingDay, k.meetingStartTime,"
//			+ " c.collectionId, c.txnDate, c.collectionType, c.handledBy, c.totalAmountCollected,"
//			+ " c.groupId, c.groupName, c.groupTotalDue, c.groupTotalAdvance, c.groupNetDue, c.groupTotalCollectionAmt,"
//			+ " c.groupCustomerId, c.customerName, c.customerFlg, c.customerTotalDue, c.customerTotalAdvance, c.customerNetDue,"
//			+ " c.customerCollectedAmt, c.customerAttendance, c.loanId, c.loanDue, k.kmId , "
//			+ " c.loanOutstandingAmt, c.loanCollectedAmt, c.loanProductId)"
//			+ " FROM GkKendraData k"
//			+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId  AND k.kendraId = c.kendraId"
//			/* + " JOIN GkMLoanProduct p ON c.loanProductId = p.productId" */
//			+ " WHERE k.branchId IN (:branchIds) AND (k.kmId = :kmId OR k.kendraId IN (:kendraIds))"
//			/* + "AND k.nextMeetingDate = c.transactionDate" */
//			+ " ORDER BY k.kendraId, k.nextMeetingDate, k.meetingDay ASC")
//	public List<CollectionDetailsDto> fetchCollectionDataForKendra(@Param("kmId") String kmId,
//			@Param("branchIds") List<String> branchIds, @Param("kendraIds") List<Integer> kendraId);
	
	@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.CollectionDetailsDto(k.kendraId, k.branchId,"
			+ " k.kendraName, k.nextMeetingDate,k.meetingDay, k.meetingStartTime,"
			+ " c.collectionId, c.txnDate, c.collectionType, c.handledBy, c.totalAmountCollected,"
			+ " c.groupId, c.groupName, c.groupTotalDue, c.groupTotalAdvance, c.groupNetDue, c.groupTotalCollectionAmt,"
			+ " c.groupCustomerId, c.customerName, c.customerFlg, c.customerTotalDue, c.customerTotalAdvance, c.customerNetDue,"
			+ " c.customerCollectedAmt, c.customerAttendance, c.loanId, c.loanDue, k.kmId , "
			+ " c.loanOutstandingAmt, c.loanCollectedAmt, c.loanProductId)" + " FROM GkKendraData k"
			+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId  AND k.kendraId = c.kendraId"
			/* + " JOIN GkMLoanProduct p ON c.loanProductId = p.productId" */
			+ " WHERE k.branchId IN (:branchIds) AND (k.kendraId IN (:kendraIds))"
			/* + "AND k.nextMeetingDate = c.transactionDate" */
			+ " ORDER BY k.kendraId, k.nextMeetingDate, k.meetingDay ASC")
	public List<CollectionDetailsDto> fetchCollectionDataForKendra(@Param("branchIds") List<String> branchIds,
			@Param("kendraIds") List<Integer> kendraId);
		
//	@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.CollectionDetailsDto(k.kendraId, k.branchId,"
//			+ " k.kendraName, k.nextMeetingDate,k.meetingDay, k.meetingStartTime,"
//			+ " c.collectionId, c.txnDate, c.collectionType, c.handledBy, c.totalAmountCollected,"
//			+ " c.groupId, c.groupName, c.groupTotalDue, c.groupTotalAdvance, c.groupNetDue, c.groupTotalCollectionAmt,"
//			+ " c.groupCustomerId, c.customerName, c.customerFlg, c.customerTotalDue, c.customerTotalAdvance, c.customerNetDue,"
//			+ " c.customerCollectedAmt, c.customerAttendance, c.loanId, c.loanDue, k.kmId , "
//			+ " c.loanOutstandingAmt, c.loanCollectedAmt, c.loanProductId)"
//			+ " FROM GkKendraData k"
//			+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId AND k.kendraId = c.kendraId"
//			/* + " JOIN GkMLoanProduct p ON c.loanProductId = p.productId" */
//			+ " WHERE k.branchId IN (:branchIds) AND k.nextMeetingDate = :meetingDate"
//			+ " AND (k.kmId = :kmId OR k.kendraId IN (:kendraIds))"
//			+ " ORDER BY k.kendraId, k.nextMeetingDate, k.meetingDay ASC")
//	public List<CollectionDetailsDto> fetchMeetingDateCollectionDataForKendra(@Param("kmId") String kmId,
//			@Param("branchIds") List<String> branchIds, @Param("kendraIds") List<Integer> kendraId, @Param("meetingDate") String metingDate);
	
	@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.CollectionDetailsDto(k.kendraId, k.branchId,"
			+ " k.kendraName, k.nextMeetingDate,k.meetingDay, k.meetingStartTime,"
			+ " c.collectionId, c.txnDate, c.collectionType, c.handledBy, c.totalAmountCollected,"
			+ " c.groupId, c.groupName, c.groupTotalDue, c.groupTotalAdvance, c.groupNetDue, c.groupTotalCollectionAmt,"
			+ " c.groupCustomerId, c.customerName, c.customerFlg, c.customerTotalDue, c.customerTotalAdvance, c.customerNetDue,"
			+ " c.customerCollectedAmt, c.customerAttendance, c.loanId, c.loanDue, k.kmId , "
			+ " c.loanOutstandingAmt, c.loanCollectedAmt, c.loanProductId)" + " FROM GkKendraData k"
			+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId AND k.kendraId = c.kendraId"
			/* + " JOIN GkMLoanProduct p ON c.loanProductId = p.productId" */
			+ " WHERE k.branchId IN (:branchIds) AND k.nextMeetingDate = :meetingDate"
			+ " AND (k.kendraId IN (:kendraIds))" + " ORDER BY k.kendraId, k.nextMeetingDate, k.meetingDay ASC")
	public List<CollectionDetailsDto> fetchMeetingDateCollectionDataForKendra(
			@Param("branchIds") List<String> branchIds, @Param("kendraIds") List<Integer> kendraId,
			@Param("meetingDate") String metingDate);
	 
	 /*@Query(value = "SELECT k.kendraId, k.BRANCHID, k.KENDRA_NAME, k.NEXT_MEETING_DATE, k.MESTARTING_TIME,"
		+ " c.Collection_ID, c.Transaction_Date, c.Collection_Type, c.Handled_By, c.Total_Amount_Collected,"
		+ " c.Group_ID, c.Group_Name, c.Group_Total_Due, c.Group_Total_Advance, c.Group_Net_Due,"
		+ " c.Group_Tot_Coll_Amount,c.Group_Customer_ID, c.Customer_Name, c.Cust_Total_Due"
		+ " c.Cust_Total_Advance, c.Cust_Net_Due,c.Cust_Collected_Amount, c.Cust_Attendance,"
		+ " c.Loan_ID, c.Loan_Due, p.SHORT_DESCRIPTION, c.Loan_Outstanding_Amount, c.Loan_Collected_Amount"
		+ " FROM gk_kendra_data k, t24_collection_sheets c, gk_m_loan_product p"
		+ " WHERE k.kendraId = c.Kendra_ID AND k.BRANCHID = c.Branch_ID"
		+ " AND k.kmid = :kmid AND k.BRANCHID = :branchId AND k.kendraId IN (:kendraIds)"
		+ " AND k.NEXT_MEETING_DATE = c.Transaction_Date AND c.Loan_Product_Id = p.PRODUCT_ID"
		+ " ORDER BY k.kendraId, k.NEXT_MEETING_DATE, k.MEETING_DAY ASC", nativeQuery = true)*/
	
		@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto(k.kendraId, k.kendraName,"
				+ " k.kendraAddr, k.kmId, k.kmName) FROM GkKendraData k"
				+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId"
				+ " AND k.kendraId = c.kendraId WHERE k.kmId IN"
				+ " (SELECT u.userId FROM GkUserData u WHERE u.hierarchyId =:branchId"
				+ " AND u.userdesignation =:userDsgn)"
				+ " AND k.branchId =:branchId AND k.nextMeetingDate =:nextMeetingDt"
				+ " AND k.kendraStatus =:kendraStatus ORDER BY k.meetingStartTime")
		public List<KendraDetailsDto> fetchKendraInfo(@Param("branchId") String branchId,
				@Param("userDsgn") String userDsgn, @Param("nextMeetingDt") String nextMeetingDt,
				@Param("kendraStatus") String kendraStatus);
		
		@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto(k.kendraId, k.kendraName,"
				+ " k.kendraAddr, k.kmId, k.kmName) FROM GkKendraData k"
				+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId"
				+ " AND k.kendraId = c.kendraId WHERE "
				+ " k.branchId =:branchId AND k.nextMeetingDate =:nextMeetingDt"
				+ " AND k.kendraStatus =:kendraStatus ORDER BY k.meetingStartTime")
		public List<KendraDetailsDto> fetchKendraInfoByRole(@Param("branchId") String branchId,
			    @Param("nextMeetingDt") String nextMeetingDt,@Param("kendraStatus") String kendraStatus);
	
		@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto(k.kendraId, k.kendraName,"
				+ " k.kendraAddr, k.kmId, k.kmName) FROM GkKendraData k"
				+ " JOIN T24CollectionSheet c ON k.branchId = c.branchId"
				+ " AND k.kendraId = c.kendraId WHERE k.kmId IN"
				+ " (SELECT u.userId FROM GkUserData u WHERE u.hierarchyId =:branchId"
				+ " AND u.userdesignation =:userDsgn)" + " AND k.branchId =:branchId"
				+ " AND k.kendraStatus =:kendraStatus ORDER BY k.meetingStartTime")
		public List<KendraDetailsDto> fetchKendraInfoForBranchId(@Param("branchId") String branchId,
				@Param("userDsgn") String userDsgn, @Param("kendraStatus") String kendraStatus);
	
	@Query(value = ("select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "	UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kmid = :userId  and kendraId NOT IN (:excludedkList) and KENDRA_STATUS=:kendraStatus AND BRANCHID =:branchId"), nativeQuery = true)
	public List<GkKendraData> findListForOnlyExcludedKendra(String userId, String kendraStatus, List<String> excludedkList,String branchId);

	@Query(value = ("select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kmid = :userId and KENDRA_STATUS=:kendraStatus "
			+ "union "
			+ "	select kendraId,KENDRA_NAME,KM_NAME,BRANCHID,VILLAGE_TYPE,KENDRA_ADDR,STATE,DISTRICT,TALUK,AREA_TYPE,VILLAGE,PINCODE,MEETING_FREQ,FIRST_MEETING_DATE,NEXT_MEETING_DATE,MEETING_DAY,MEETING_PLACE,MESTARTING_TIME,ENDING_TIME,DISTANCE,LEADER,SECRETARY,CREATED_BY,created_ts, "
			+ "			UPDATED_BY,KENDRA_STATUS,ACTIVATION_DATE,KMID from gk_kendra_data where kendraId IN (:kList) and KENDRA_STATUS=:kendraStatus"), nativeQuery = true)
	public List<GkKendraData> findListForOnlyIncludedKendra(String userId, String kendraStatus,List<String> kList);
}
