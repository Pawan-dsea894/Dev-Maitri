package com.iexceed.appzillonbanking.core.config;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Configuration
public class MDCFilterConfig implements WebFilter {

	private static final Logger LOG = LogManager.getLogger(MDCFilterConfig.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		return chain.filter(exchange)
				//.doOnRequest(r -> logWithContext(headers, httpHeaders -> LOG.info("Some message with MDC set")))
				.contextWrite(c -> Context.of(headers)).doFinally(r -> removeMDC(headers));

	}

	private void removeMDC(HttpHeaders headers) {
		if (headers.containsKey("userId") && null != headers.get("userId")) {
			MDC.remove(headers.get("userId").toString());
		}
		/*
		 * headers.keySet().forEach(val -> { if (val.equalsIgnoreCase("userId")) {
		 * MDC.remove(val); } });
		 */
	}

	public static void logWithContext(HttpHeaders headers, Consumer<HttpHeaders> logAction) {

		if (headers.containsKey("userId") && null != headers.getFirst("userId")) {
			MDC.put("userId", headers.getFirst("userId"));
		}
		/*
		 * headers.forEach((name, values) -> { if (name.equalsIgnoreCase("userId")) {
		 * MDC.put(name, values.get(0)); } });
		 */
		logAction.accept(headers);
	}
}
