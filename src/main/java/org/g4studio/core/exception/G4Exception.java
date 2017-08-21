package org.g4studio.core.exception;

import org.g4studio.core.util.G4Constants;

/**
 * G4公共异常类<br>
 */
@SuppressWarnings("all")
public class G4Exception extends RuntimeException {

	public G4Exception() {
		super();
	}

	public G4Exception(String msg) {
		super(G4Constants.Exception_Head + msg);
	}
}
