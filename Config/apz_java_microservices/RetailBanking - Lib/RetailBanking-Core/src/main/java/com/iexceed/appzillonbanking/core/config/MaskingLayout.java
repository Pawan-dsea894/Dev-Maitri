package com.iexceed.appzillonbanking.core.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingLayout extends PatternLayout {

	private static final Logger logger = LogManager.getLogger(MaskingLayout.class);

	private Pattern multilinePattern;
	private List<String> maskPatterns = new ArrayList<>();

	private List<String> maskingKeywords = new ArrayList<>();

	public MaskingLayout() {
		loadMaskingKeywords();
	}

	private void loadMaskingKeywords() {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			Properties properties = new Properties();
			properties.load(input);

			String patterns = properties.getProperty("masking.patterns");
			if (patterns != null) {
				maskingKeywords.addAll(Arrays.asList(patterns.split(",")));
			}
		} catch (Exception e) {
			logger.error("Exception in MaskingLayout" + e);
		}
	}

	public void addMaskPattern(String maskPattern) {
		maskPatterns.add(maskPattern);
		multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")),
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	}

	@Override
	public String doLayout(ILoggingEvent event) {
		return maskXMLContent(super.doLayout(event));
	}

	private String maskMessage(String message) {
		if (multilinePattern == null) {
			return message;
		}
		StringBuilder sb = new StringBuilder(message);
		Matcher matcher = multilinePattern.matcher(sb);
		while (matcher.find()) {
			IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
				if (matcher.group(group) != null) {
					IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
				}
			});
		}
		return sb.toString();
	}

	private String maskXMLContent(String message) {
		for (String keyword : maskingKeywords) {
			message = maskSensitiveData(message, keyword);
		}
		return maskMessage(message);
	}

	private String maskSensitiveData(String input, String keyword) {
		String regex = "<" + keyword + ">([\\s\\S]*?)</" + keyword + ">";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String maskedContent = maskContent(matcher.group(1));
			matcher.appendReplacement(result, "<" + keyword + ">" + maskedContent + "</" + keyword + ">");
		}
		matcher.appendTail(result);
		return result.toString();
	}

	private String maskContent(String content) {
		// Implement your logic for masking content
		// Here, we simply replace all characters with '*'
		StringBuilder rep = new StringBuilder();
		for (int i = 0; i < content.length(); i++) {
			rep.append("*");
		}
		return rep.toString();
	}
}
