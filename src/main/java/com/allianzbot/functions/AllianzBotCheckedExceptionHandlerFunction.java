package com.allianzbot.functions;

@FunctionalInterface
public interface AllianzBotCheckedExceptionHandlerFunction<T, R, E extends Exception> {

	R apply(T t) throws E;
}