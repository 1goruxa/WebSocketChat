package main.api.Response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class RegisterResponse {

    private boolean result;
    private String error;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
