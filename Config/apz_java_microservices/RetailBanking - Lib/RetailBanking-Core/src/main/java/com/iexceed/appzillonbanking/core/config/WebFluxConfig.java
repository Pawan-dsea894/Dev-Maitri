package com.iexceed.appzillonbanking.core.config;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

	private static final Logger LOG = LogManager.getLogger(WebFluxConfig.class);
	
	@Value("${spring.codec.max-in-memory-size}")
    private int maxInMemorySize;

	@Bean
	public WebClient getWebClient() {
		HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 90000)
				.doOnConnected(val -> {
					val.addHandlerLast(new ReadTimeoutHandler(90));
					val.addHandlerLast(new WriteTimeoutHandler(90));
				}).responseTimeout(Duration.ofSeconds(90));

		httpClient.compress(true);
		httpClient.keepAlive(false);
		httpClient.wiretap(true);
		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
		return WebClient.builder().clientConnector(connector)
				//.filter(logRequest())
				//.filter(logResponse())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
	}

	/**
	 * Logging Mechanism for WebFlux Request and Response
	 * 
	 * @author akshay.upadhya
	 * @since 26.07.2022
	 * @return
	 */
	public static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(request -> {
			if (LOG.isDebugEnabled()) {
				logRequestMethodAndUrl(request);
				logRequestHeaders(request);
			}
			LOG.debug("Request Body is {}", request);
			return Mono.just(request);
		});
	}

	private static void logRequestMethodAndUrl(ClientRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.method().name());
		sb.append(" TO ");
		sb.append(request.url());
		LOG.debug(sb.toString());
	}

	private static void logRequestHeaders(ClientRequest request) {
		request.headers().forEach((name, values) -> {
			values.forEach(value -> {
				logNameAndValuePair(name, value);
			});
		});
	}

	private static void logNameAndValuePair(String name, String value) {
		LOG.debug("{}={}", name, value);
	}

	public static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if (LOG.isDebugEnabled()) {
				logResponseStatus(response);
				logResponseHeaders(response);
			}
			return logResponseBody(response);
		});
	}

	private static void logResponseStatus(ClientResponse response) {
		LOG.debug("Response Staus Code {}", response.statusCode().value());
	}

	private static void logResponseHeaders(ClientResponse response) {
		response.headers().asHttpHeaders().forEach((name, values) -> {
			values.forEach(value -> {
				logNameAndValuePair(name, value);
			});
		});
	}

	private static Mono<ClientResponse> logResponseBody(ClientResponse response) {
		if (response.statusCode() != null
				&& (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError())) {
			return response.bodyToMono(String.class).flatMap(body -> {
				LOG.debug("Response Body is {}", body);
				return Mono.just(response);
			});
		} else {
			return Mono.just(response);
		}
	}
	
	@Override    
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {        
	    // Increase the maximum in-memory size for a single buffer       
	    configurer.defaultCodecs().maxInMemorySize(maxInMemorySize * 1024 * 1024); 
	}
}