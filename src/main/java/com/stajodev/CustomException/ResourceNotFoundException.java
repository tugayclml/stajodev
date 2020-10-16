package com.stajodev.CustomException;

public class ResourceNotFoundException extends  Exception{

    public ResourceNotFoundException(){}

    private String exceptionString;

    public ResourceNotFoundException(String exception){
        this.exceptionString = exceptionString;
    }

    public String getExceptionString() {
        return exceptionString;
    }

    public void setExceptionString(String exceptionString) {
        this.exceptionString = exceptionString;
    }

    @Override
    public String toString() {
        return "ResourceNotFoundException{" +
                "exceptionString='" + exceptionString + '\'' +
                '}';
    }
}
