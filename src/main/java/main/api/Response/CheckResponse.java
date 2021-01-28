package main.api.Response;

public class CheckResponse {

    //Позже в возврат подгружать данные пользователя (картинка и прочее)
    private boolean result;
    private String Error;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }
}
