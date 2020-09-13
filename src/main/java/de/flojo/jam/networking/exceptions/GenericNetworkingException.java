package de.flojo.jam.networking.exceptions;

/**
 * Thrown if there is any problem with the connection
 * 
 * @author Florian Sihler
 * @version 1.0, 05/19/2020
 * 
 * @since 1.1
 */
public class GenericNetworkingException extends RuntimeException {

    private static final long serialVersionUID = -7131605913846543735L;

    public GenericNetworkingException(String message) {
        super(message);
    }

}
