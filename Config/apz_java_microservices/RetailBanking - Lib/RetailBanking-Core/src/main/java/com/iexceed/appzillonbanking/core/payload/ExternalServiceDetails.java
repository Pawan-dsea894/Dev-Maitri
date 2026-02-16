package com.iexceed.appzillonbanking.core.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalServiceDetails {
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "url")
	private String url;

	@XmlElement(name = "type")
	private String type;

	@XmlElement(name = "tracinglevel")
	private String tracinglevel;

}
