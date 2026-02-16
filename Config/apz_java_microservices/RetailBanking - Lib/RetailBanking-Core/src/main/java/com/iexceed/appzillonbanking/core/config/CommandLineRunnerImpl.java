package com.iexceed.appzillonbanking.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

	@Autowired
	private CommonProperties commonProperties;

	@Autowired
	private ExternalProperties externalProperties;

	@Override
	public void run(String... args) throws Exception {
		CommonUtils.initializeCommonProperties(commonProperties.getCommon());
		CommonUtils.initializeExternalProperties(externalProperties.getCommon());
	}
}
