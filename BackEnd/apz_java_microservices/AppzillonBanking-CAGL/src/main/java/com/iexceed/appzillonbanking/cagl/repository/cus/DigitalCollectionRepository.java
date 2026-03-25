package com.iexceed.appzillonbanking.cagl.repository.cus;

import com.iexceed.appzillonbanking.cagl.entity.DigitalCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DigitalCollectionRepository extends JpaRepository<DigitalCollection, Long> {

//    @Query("""
//    select dc.amount
//    from DigitalCollection dc
//    where dc.customerId = :customerId
//      and dc.trnPostedAt >= concat(:meetingDate, ' 00:00:00')
//      and dc.trnPostedAt < concat(
//            function('DATE_FORMAT',
//                     function('DATE_ADD', :meetingDate, 1),
//                     '%Y-%m-%d'),
//            ' 00:00:00'
//      )
//""")
//    Double findMahiAmountByCustomerIdAndMeetingDate(
//            @Param("customerId") String customerId,
//            @Param("meetingDate") String meetingDate);

    @Query("""
    select dc.amount
    from DigitalCollection dc
    where dc.customerId = :customerId
      and dc.trnPostedAt >= :startDateTime
      and dc.trnPostedAt < :endDateTime
""")
    Double findMahiAmountByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startDateTime") String startDateTime,
            @Param("endDateTime") String endDateTime);


//    @Query("""
//       select dc.amount
//       from DigitalCollection dc
//       where dc.customerId = :customerId
//         and dc.trnPostedAt = (
//             select max(d.trnPostedAt)
//             from DigitalCollection d
//             where d.customerId = :customerId
//         )
//       """)
//    Double findLatestMahiAmountByCustomerId(@Param("customerId") String customerId);

}
