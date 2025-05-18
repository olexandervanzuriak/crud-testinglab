package edu.vanzuriak.crud_operations.utils;

/*
    @author  olexander
    @project crud_operations
    @class   Utils
    @version 1.0.0
    @since 5/18/25 - 09 - 48
*/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static String toJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
