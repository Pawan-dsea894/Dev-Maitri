/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillonbanking.kendra.domain.cus;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_USER_DEVICES")
public class TbAsmiUserDevices implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@JsonProperty("id")
	protected TbAsmiUserDevicesPK id;

	@Column(name = "CREATE_USER_ID")
	@JsonProperty("createUserId")
	private String createUserId;
	@Column(name = "CREATE_TS")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	@JsonProperty("createTs")
	private Date createTs;
	@Column(name = "VERSION_NO")
	private Integer versionNo;
	@Column(name = "DEVICE_STATUS")
	@JsonProperty("deviceStatus")
	private String deviceStatus;

	@Column(name = "NOTIF_REG_ID")
	private String notifRegId;

	public TbAsmiUserDevices() {
	}

	public TbAsmiUserDevices(TbAsmiUserDevicesPK id) {
		this.id = id;
	}

	public TbAsmiUserDevices(String deviceId, String userId, String appId) {
		this.id = new TbAsmiUserDevicesPK(deviceId, userId, appId);
	}

	public TbAsmiUserDevicesPK getTbAsmiUserDevicesPK() {
		return id;
	}

	public void setTbAsmiUserDevicesPK(TbAsmiUserDevicesPK id) {
		this.id = id;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public TbAsmiUserDevicesPK getId() {
		return id;
	}

	public void setId(TbAsmiUserDevicesPK id) {
		this.id = id;
	}

	public String getNotifRegId() {
		return notifRegId;
	}

	public void setNotifRegId(String notifRegId) {
		this.notifRegId = notifRegId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TbAsmiUserDevices)) {
			return false;
		}
		TbAsmiUserDevices other = (TbAsmiUserDevices) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
