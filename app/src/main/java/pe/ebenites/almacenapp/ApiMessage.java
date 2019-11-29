package pe.ebenites.almacenapp;

public class ApiMessage {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiMessage [message=" + message + "]";
    }

}
