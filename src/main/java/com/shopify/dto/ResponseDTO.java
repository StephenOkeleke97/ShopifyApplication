package com.shopify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class that represents a data transfer object for sending responses to
 * clients. Contains 3 parts; a message, an error field which indicates if
 * request resulted in an error, and finally a data field. Null fields are not
 * sent as part of response.
 * 
 * @author stephen
 *
 */
@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO {
	/**
	 * Message to be returned to client.
	 */
	private String message;
	/**
	 * Request error flag. True if request resulted in an error or false otherwise.
	 */
	private boolean error;
	private Object data;

	/**
	 * Constructs an instance of this class without any parameters.
	 */
	public ResponseDTO() {
		super();
	}

	/**
	 * Constructs an instance of this class with specified arguments.
	 * 
	 * @param message message to send to client
	 * @param error   indication of error status of request
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
