package edu.eci.arsw.blueprints.model;

public record ApiResponse<T>(int code, String message, T data) {
    public ApiResponse(int code, String message, T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }
}
