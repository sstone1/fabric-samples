/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

public interface Wallet {
	
	public boolean exists(String identityName) throws Exception;

	public Identity get(String identityName) throws Exception;

	public void put(Identity identity) throws Exception;
	
}