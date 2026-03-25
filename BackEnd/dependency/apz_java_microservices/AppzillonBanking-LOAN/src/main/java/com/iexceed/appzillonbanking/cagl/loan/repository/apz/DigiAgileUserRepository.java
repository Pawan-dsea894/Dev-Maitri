package com.iexceed.appzillonbanking.cagl.loan.repository.apz;

import com.iexceed.appzillonbanking.cagl.loan.domain.apz.DigiAgileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigiAgileUserRepository extends JpaRepository<DigiAgileUser, String> {
}
