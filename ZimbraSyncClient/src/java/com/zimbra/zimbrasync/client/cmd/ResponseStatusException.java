/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

@SuppressWarnings("serial")
public class ResponseStatusException extends Exception {
	
	public static enum ErrorCode {
		FolderExists,
		FolderImmutable,
		FolderNotFound,
		ParentNotFound,
		ServerError,
		AccessDenied,
		RequestTimedOut,
		InvalidSyncKey,
		MalformedRequest,
		UnknownError,
		
	};
	
	private ErrorCode errorCode;
	
	public ResponseStatusException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	
	public static ResponseStatusException FolderExists() {
	    return new ResponseStatusException(ErrorCode.FolderExists);
	}

	public static ResponseStatusException FolderImmutable() {
	    return new ResponseStatusException(ErrorCode.FolderImmutable);
	}

	public static ResponseStatusException FolderNotFound() {
	    return new ResponseStatusException(ErrorCode.FolderNotFound);
	}

	public static ResponseStatusException ParentNotFound() {
	    return new ResponseStatusException(ErrorCode.ParentNotFound);
	}

	public static ResponseStatusException ServerError() {
	    return new ResponseStatusException(ErrorCode.ServerError);
	}

	public static ResponseStatusException AccessDenied() {
	    return new ResponseStatusException(ErrorCode.AccessDenied);
	}

	public static ResponseStatusException RequestTimedOut() {
	    return new ResponseStatusException(ErrorCode.RequestTimedOut);
	}

	public static ResponseStatusException InvalidSyncKey() {
	    return new ResponseStatusException(ErrorCode.InvalidSyncKey);
	}

	public static ResponseStatusException MalformedRequest() {
	    return new ResponseStatusException(ErrorCode.MalformedRequest);
	}

	public static ResponseStatusException UnknownError() {
	    return new ResponseStatusException(ErrorCode.UnknownError);
	}

	
	
	public boolean isFolderExists() {
	    return errorCode == ErrorCode.FolderExists;
	}

	public boolean isFolderImmutable() {
	    return errorCode == ErrorCode.FolderImmutable;
	}

	public boolean isFolderNotFound() {
	    return errorCode == ErrorCode.FolderNotFound;
	}

	public boolean isParentNotFound() {
	    return errorCode == ErrorCode.ParentNotFound;
	}

	public boolean isServerError() {
	    return errorCode == ErrorCode.ServerError;
	}

	public boolean isAccessDenied() {
	    return errorCode == ErrorCode.AccessDenied;
	}

	public boolean isRequestTimedOut() {
	    return errorCode == ErrorCode.RequestTimedOut;
	}

	public boolean isInvalidSyncKey() {
	    return errorCode == ErrorCode.InvalidSyncKey;
	}

	public boolean isMalformedRequest() {
	    return errorCode == ErrorCode.MalformedRequest;
	}

	public boolean isUnknownError() {
	    return errorCode == ErrorCode.UnknownError;
	}
}
