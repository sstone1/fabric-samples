/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.io.Serializable;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

public class Identity implements User, Serializable {
	
	private static final long serialVersionUID = 2090560797643185251L;
	private String name;
	private Set<String> roles;
	private String account;
	private String affiliation;
	private String mspId;
	private Enrollment enrollment;

	public Identity() {
		
	}

	public Identity(String name, String mspId, Enrollment enrollment) {
		this.name = name;
		this.mspId = mspId;
		this.enrollment = enrollment;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Set<String> getRoles() {
		return this.roles;
	}

	public void setName(Set<String> roles) {
		this.roles = roles;
	}

	@Override
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	@Override
	public Enrollment getEnrollment() {
		return this.enrollment;
	}

	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
	}

	@Override
	public String getMspId() {
		return this.mspId;
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
	}
	
}