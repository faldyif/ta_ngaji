package com.preklit.ngaji.entities;

import java.util.List;
import java.util.Map;

/**
 * Created by Faldy on 4/17/2018.
 */

public class ApiError {

    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
