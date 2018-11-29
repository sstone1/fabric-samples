/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;

public class Query {

	public static void main(String[] args) throws Exception {

		// Create a new file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		FileSystemWallet wallet = new FileSystemWallet(walletPath);
		System.out.println("Wallet path: " + walletPath.toString());

		// Check to see if we"ve already enrolled the user.
		boolean userExists = wallet.exists("user1");
		if (!userExists) {
			System.err.println("An identity for the user \"user1\" does not exist in the wallet");
			System.err.println("Run the RegisterUser.java application before retrying");
			return;
		}

		// Create a new client for connecting to our peer node.
		HFClient client = HFClient.createNewInstance();
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		client.setCryptoSuite(cryptoSuite);
		Identity userIdentity = wallet.get("user1");
		client.setUserContext(userIdentity);
		Path networkConfigPath = Paths.get("..", "..", "basic-network", "connection.json");
		NetworkConfig networkConfig = NetworkConfig.fromJsonFile(networkConfigPath.toFile());

		// Get the channel our chaincode is deployed to.
		Channel channel = client.loadChannelFromConfig("mychannel", networkConfig);
		channel.initialize();

		// Submit a proposal request for the specified transaction to our peer node.
        // queryCar transaction - requires 1 argument, ex: ('queryCar', 'CAR4')
        // queryAllCars transaction - requires no arguments, ex: ('queryAllCars')
		TransactionProposalRequest proposalRequest = client.newTransactionProposalRequest();
		proposalRequest.setChaincodeID(ChaincodeID.newBuilder().setName("fabcar").build());
		proposalRequest.setFcn("queryAllCars");
		proposalRequest.setArgs(new String[] {});
		Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(proposalRequest);

		// Validate the proposal responses.
		if (proposalResponses.size() < 1) {
			System.err.println("No proposal responses received");
			return;
		}
		ProposalResponse proposalResponse = proposalResponses.iterator().next();
		if (!proposalResponse.getStatus().equals(ChaincodeResponse.Status.SUCCESS)) {
			System.err.println("The proposal response was not successful");
			System.err.println("Message: " + proposalResponse.getMessage());
			return;
		}
		System.out.println(new String(proposalResponse.getChaincodeActionResponsePayload(), StandardCharsets.UTF_8));

	}

}