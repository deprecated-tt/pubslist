package com.alco.pubslist.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "places")
public class Place {

	@Id
	@GeneratedValue(strategy = SEQUENCE)
	private Integer id;
	private String name;
	private String address;
	private String latitude;
	private String longitude;

	@Column(name = "owner_id")
	private Integer ownerId;

	private boolean approved;

	private boolean enabled;

	public Place() {

	}

	public Integer getId() {

		return id;
	}

	public void setId(Integer id) {

		this.id = id;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getAddress() {

		return address;
	}

	public void setAddress(String address) {

		this.address = address;
	}

	public String getLatitude() {

		return latitude;
	}

	public void setLatitude(String coordinate_1) {

		this.latitude = coordinate_1;
	}

	public String getLongitude() {

		return longitude;
	}

	public void setLongitude(String coordinate_2) {

		this.longitude = coordinate_2;
	}

	public boolean isApproved() {

		return approved;
	}

	public void setApproved(boolean approved) {

		this.approved = approved;
	}

	public boolean isEnabled() {

		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	public Integer getOwnerId() {

		return ownerId;
	}

	public void setOwnerId(Integer owner_id) {

		this.ownerId = owner_id;
	}

	public boolean isPlaceOwnedByUser(String userId) {

		return getOwnerId().toString().equals(userId);
	}
}
