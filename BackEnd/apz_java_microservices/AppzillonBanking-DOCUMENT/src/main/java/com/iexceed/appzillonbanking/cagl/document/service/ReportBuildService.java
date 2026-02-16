package com.iexceed.appzillonbanking.cagl.document.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.springframework.stereotype.Service;

	@Service
	public class ReportBuildService {
		public byte[] generateAndDownloadSanctionReport(String applicationId) {
			try {
				// TODO: implement report generation logic here
				byte[] reportData = new byte[0];
				return reportData;
			} catch (Exception e) {
				return null;
			}
		}
	}
