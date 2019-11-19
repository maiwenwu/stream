package com.tech.mediaserver.exception;

/**
 * @author chudichen
 * @date 2018/10/30
 */
public class ConflictException extends RuntimeException {

    public ConflictException() {

    }

    public ConflictException(String msg) {
        super(msg);
    }
}
