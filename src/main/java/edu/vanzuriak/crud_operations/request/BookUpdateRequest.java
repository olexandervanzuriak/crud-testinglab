package edu.vanzuriak.crud_operations.request;

/*
    @author  olexander
    @project crud_operations
    @class   BookUpdateRequest
    @version 1.0.0
    @since 5/10/25 - 09 - 46
*/


public record BookUpdateRequest(String id, String name, String author, String description) {

}
