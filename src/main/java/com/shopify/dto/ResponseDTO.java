package com.shopify.dto;

public class ResponseDTO {
	/**
	 * Message to be returned to client.
	 */
	private String message;
	/**
	 * Request error flag. True if request resulted in
	 * an error or false otherwise.
	 */
	private boolean error;

	public ResponseDTO() {
		super();
	}

	/**
	 * @param message
	 * @param error
	 */
	public ResponseDTO(String message, boolean error) {
		super();
		this.message = message;
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
}
