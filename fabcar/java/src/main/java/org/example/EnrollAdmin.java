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

public class EnrollAdmin {
	
    public static void main(String[] args) throws Exception {
    	
    	// Create a CA client for interacting with the CA.
        HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", null);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);
        
		// Create a new file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		FileSystemWallet wallet = new FileSystemWallet(walletPath);
		System.out.println("Wallet path: " + walletPath.toString());

		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("admin");
        if (adminExists) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }
        
        // Enroll the admin user, and import the new identity into the wallet.
        Enrollment enrollment = caClient.enroll("admin", "adminpw");
        Identity user = new Identity("admin", "Org1MSP", enrollment);
        wallet.put(user);
        System.out.println("Successfully enrolled admin user \"admin\" and imported it into the wallet");
        
    }
    
}