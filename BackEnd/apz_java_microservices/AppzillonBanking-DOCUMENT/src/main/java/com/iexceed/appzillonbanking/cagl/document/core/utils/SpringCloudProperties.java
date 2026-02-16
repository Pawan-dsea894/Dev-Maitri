package com.iexceed.appzillonbanking.cagl.document.core.utils;

public enum SpringCloudProperties {
	
	LIT_FILE_PATH("litFilePath", "LIT File Path"),
	LIT_FILE_FORMAT("litFileFormat", "LIT File Format"),
	PROP_FILE_PATH("cobFlags", "COB Properties file");
	
	
	private final String key;
	private final String value;
	
	SpringCloudProperties(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}