package com.planet_learning.api_response;

public class ApiResponse<T>
{
	private Boolean status;
	private String message;
	private T data;
	
	public ApiResponse() {}
	
	public ApiResponse(Boolean status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> ApiResponse<T> success(String message, T data)
	{
		return new ApiResponse<T>(true, message, data);
	}
	
	public static <T> ApiResponse<T> error(String message)
	{
		return new ApiResponse<T>(true, message, null);
	}
}
