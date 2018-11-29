/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FabCar extends ChaincodeBase {
	
	@Override
	public Chaincode.Response init(ChaincodeStub stub) {
		System.out.println("=========== Instantiated fabcar chaincode ===========");
		return ChaincodeBase.newSuccessResponse();
	}

	@Override
	public Chaincode.Response invoke(ChaincodeStub stub) {
		String fcn = stub.getFunction();
		List<String> params = stub.getParameters();
		System.out.printf("%s %s\n", fcn, params.toArray());
		try {
			if (fcn.equals("initLedger")) {
				return this.initLedger(stub);
			} else if (fcn.equals("queryCar")) {
				return this.queryCar(stub, params);
			} else if (fcn.equals("createCar")) {
				return this.createCar(stub, params);
			} else if (fcn.equals("queryAllCars")) {
				return this.queryAllCars(stub);
			} else if (fcn.equals("changeCarOwner")) {
				return this.changeCarOwner(stub, params);
			}
			return ChaincodeBase.newErrorResponse("Invalid Smart Contract function name.");
		} catch (Exception e) {
			return ChaincodeBase.newErrorResponse(e);
		}
	}

	public Chaincode.Response initLedger(ChaincodeStub stub) {
		System.out.println("============= START : Initialize Ledger ===========");
		Car[] cars = new Car[] {
			new Car("blue", "Toyota", "Prius", "Tomoko"),
			new Car("red", "Ford", "Mustang", "Brad"),
			new Car("green", "Hyundai", "Tucson", "Jin Soo"),
			new Car("yellow", "Volkswagen", "Passat", "Max"),
			new Car("black", "Tesla", "S", "Adriana"),
			new Car("purple", "Peugeot", "205", "Michel"),
			new Car("white", "Chery", "S22L", "Aarav"),
			new Car("violet", "Fiat", "Punto", "Pari"),
			new Car("indigo", "Tata", "Nano", "Valeria"),
			new Car("brown", "Holden", "Barina", "Shotaro")
		};
		for (int i = 0; i < cars.length; i++) {
			cars[i].docType = "car";
			stub.putState("CAR" + i, new Gson().toJson(cars[i]).getBytes(StandardCharsets.UTF_8));
			System.out.println("Added <--> " + cars[i]);
		}
		return ChaincodeBase.newSuccessResponse();
	}

	public Chaincode.Response queryCar(ChaincodeStub stub, List<String> args) {
		if (args.size() != 1) {
			return ChaincodeBase.newErrorResponse("Incorrect number of arguments. Expecting 1");
		}
		String carNumber = args.get(0);
		byte[] carAsBytes = stub.getState(carNumber);
		return ChaincodeBase.newSuccessResponse(carAsBytes);
	}

	public Chaincode.Response createCar(ChaincodeStub stub, List<String> args) {
		if (args.size() != 5) {
			return ChaincodeBase.newErrorResponse("Incorrect number of arguments. Expecting 5");
		}
		Car car = new Car(args.get(3), args.get(1), args.get(2), args.get(4));
		byte[] carAsBytes = new Gson().toJson(car).getBytes(StandardCharsets.UTF_8);
		stub.putState(args.get(0), carAsBytes);
		return ChaincodeBase.newSuccessResponse();
	}

	public Chaincode.Response queryAllCars(ChaincodeStub stub) throws Exception {
		String startKey = "CAR0";
		String endKey = "CAR999";
		try (QueryResultsIterator<KeyValue> resultsIterator = stub.getStateByRange(startKey, endKey)) {
			JsonArray resultsArray = new JsonArray();
			for (KeyValue keyValue : resultsIterator) {
				JsonObject resultObject = new JsonObject();
				resultObject.addProperty("Key", keyValue.getKey());
				resultObject.addProperty("Record", keyValue.getStringValue());
				resultsArray.add(resultObject);
			}
			Chaincode.Response response = ChaincodeBase.newSuccessResponse(resultsArray.toString().getBytes(StandardCharsets.UTF_8));
			return response;
		}
	}

	public Chaincode.Response changeCarOwner(ChaincodeStub stub, List<String> args) {
		if (args.size() != 2) {
			return ChaincodeBase.newErrorResponse("Incorrect number of arguments. Expecting 2");
		}
		byte[] carAsBytes = stub.getState(args.get(0));
		Car car = new Gson().fromJson(new String(carAsBytes, StandardCharsets.UTF_8), Car.class);
		car.owner = args.get(1);
		carAsBytes = new Gson().toJson(car).getBytes(StandardCharsets.UTF_8);
		stub.putState(args.get(0), carAsBytes);
		return ChaincodeBase.newSuccessResponse();
	}
	
}