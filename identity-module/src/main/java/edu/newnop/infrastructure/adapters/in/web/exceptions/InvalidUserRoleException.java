package edu.newnop.infrastructure.adapters.in.web.exceptions;

public class InvalidUserRoleException extends RuntimeException{
    public InvalidUserRoleException (String message){
        super(message);
    }
}
