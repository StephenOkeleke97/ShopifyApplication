package com.shopify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
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
	private Object data;

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
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
