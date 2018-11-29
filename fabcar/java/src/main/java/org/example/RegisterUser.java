/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class RegisterUser {
	
	public static void main(String[] args) throws Exception {

		// Create a new file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		FileSystemWallet wallet = new FileSystemWallet(walletPath);
		System.out.println("Wallet path: " + walletPath.toString());

		// Check to see if we've already enrolled the user.
		boolean userExists = wallet.exists("user1");
		if (userExists) {
			System.out.println("An identity for the user \"user1\" already exists in the wallet");
			return;
		}
		
		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("admin");
		if (!adminExists) {
			System.out.println("An identity for the admin user \"admin\" does not exist in the wallet");
			System.out.println("Run the EnrollAdmin.java application before retrying");
			return;
		}
		
		// Create a CA client for interacting with the CA.
		HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);
		Identity adminIdentity = wallet.get("admin");
		
		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest("user1");
		registrationRequest.setAffiliation("org1.department1");
		registrationRequest.setEnrollmentID("user1");
		String enrollmentSecret = caClient.register(registrationRequest, adminIdentity);
		Enrollment enrollment = caClient.enroll("user1", enrollmentSecret);
		Identity user = new Identity("user1", "Org1MSP", enrollment);
		wallet.put(user);
		System.out.println("Successfully enrolled user \"user1\" and imported it into the wallet");
		
	}
	
}