package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.T24CollectionSheet;
import com.iexceed.appzillonbanking.cagl.entity.T24CollectionSheetPK;

@Repository
public interface T24CollectionSheetRepository extends JpaRepository<T24CollectionSheet, T24CollectionSheetPK> {
}
