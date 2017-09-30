package be.dijlezonen.dzwelg.exceptions;

public class BedragException extends Exception {

    static final long serialVersionUID = 42L;

    BedragException() {
        super();
    }

    public BedragException(String message) {
        super(message);
    }
}
