package com.dexxf.iris.common.result;

public record Result<T,E>(boolean success,T data, E error) {

    public static <T,E> Result<T,E> ok(T data) {
        return new Result<>(true,data, null);
    }

    public static <T,E> Result<T,E> fail(E error) {
        return new Result<>(false,null,error);
    }

}
