package com.iexceed.appzillonbanking.kendra.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserDevices;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserDevicesPK;

public interface TbAsmiUserDeviceRepo extends JpaRepository<TbAsmiUserDevices, TbAsmiUserDevicesPK> {

}
