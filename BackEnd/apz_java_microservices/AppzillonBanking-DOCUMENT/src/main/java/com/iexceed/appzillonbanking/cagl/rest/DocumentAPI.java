package com.iexceed.appzillonbanking.cagl.rest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iexceed.appzillonbanking.cagl.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/document", name = "DOCUMENT")
@RequestMapping("application/document")
public class DocumentAPI {

	private static final Logger logger = LogManager.getLogger(DocumentAPI.class);

	@Autowired
	private DocumentService documentService;


	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Download Sanction Reports", description = "API to Download Sanction Reports")
	@GetMapping(value = "/downloadSanctionReport")
	public Mono<ResponseEntity<byte[]>> downloadSanctionReportHis(@RequestParam("id") String applicationId) {
		logger.warn("Start : downloadSactionReport with applicationId :: {}", applicationId);

		return documentService.callandGenerateKFSScheuduleHis(applicationId, "KFS").flatMap(fileObjBytes -> {

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SanctionReport.html");
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

			return Mono.just(ResponseEntity.ok().headers(headers).body(fileObjBytes));
		});

	}
}