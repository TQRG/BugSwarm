package sic.repository;

public class XMLException extends Exception {    

    public XMLException() {
        super();
    }

    public XMLException(String mensaje) {
        super(mensaje);
    }

    public XMLException(Throwable causa) {
        super(causa);
    }

    public XMLException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}