package com.studentApp.util;

import java.util.concurrent.Callable;

public interface CancellableCallable<E> extends Callable<E>, Cancellable {
}
