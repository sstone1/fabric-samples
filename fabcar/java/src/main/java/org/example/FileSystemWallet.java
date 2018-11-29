/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemWallet implements Wallet {

	private Path walletPath;

	public FileSystemWallet(Path walletPath) throws Exception {
		boolean walletExists = Files.exists(walletPath);
		if (!walletExists) {
			Files.createDirectories(walletPath);
		}
		this.walletPath = walletPath;
	}

	@Override
	public boolean exists(String identityName) throws Exception {
		Path identityFile = this.walletPath.resolve(identityName + ".bin");
		return Files.exists(identityFile);
	}

	@Override
	public Identity get(String identityName) throws Exception {
		Path identityFile = this.walletPath.resolve(identityName + ".bin");
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(identityFile))) {
			Object obj = ois.readObject();
			return (Identity) obj;
		}
	}

	@Override
	public void put(Identity identity) throws Exception {
		Path identityFile = this.walletPath.resolve(identity.getName() + ".bin");
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(identityFile))) {
			oos.writeObject(identity);
		}
	}

}