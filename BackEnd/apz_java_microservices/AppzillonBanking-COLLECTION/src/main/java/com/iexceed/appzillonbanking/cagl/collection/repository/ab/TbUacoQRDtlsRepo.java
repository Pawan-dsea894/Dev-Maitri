package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoQRDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoQRDtlsId;

public interface TbUacoQRDtlsRepo extends CrudRepository<TbUacoQRDtls, TbUacoQRDtlsId> {

	Optional<TbUacoQRDtls> findByBillNumber(String billNumber);

	List<TbUacoQRDtls> findByCustomerIdIn(List<String> customerId);
}
