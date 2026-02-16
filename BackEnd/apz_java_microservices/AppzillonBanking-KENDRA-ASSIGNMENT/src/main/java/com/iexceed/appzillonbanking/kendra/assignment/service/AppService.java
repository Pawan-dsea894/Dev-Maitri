package com.iexceed.appzillonbanking.kendra.assignment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.kendra.assignment.entty.KendraManagementEntity;
import com.iexceed.appzillonbanking.kendra.assignment.repo.KendraRepo;

@Service
public class AppService {

	@Autowired
	KendraRepo repo;
	
	@Value("${kendra.unassined}")
	String assignedSatus;

	public List<KendraManagementEntity> getList(String kmid)
	{
		List<String> kList = new ArrayList<String>();
		kList.add(kmid);
		kList.add(assignedSatus);
		return repo.fetchKendraMangerAsignList(kList);
	}

}
