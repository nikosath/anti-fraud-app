package antifraud.error;

import io.vavr.control.Either;

public class Result {

    public static <L, R> Either<L, R> success(R object) {
        return Either.right(object);
    }

    public static <L, R> Either<L, R> error(L object) {
        return Either.left(object);
    }

}
