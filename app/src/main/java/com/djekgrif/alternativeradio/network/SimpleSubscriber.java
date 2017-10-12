package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.common.Logger;

import rx.Subscriber;

/**
 * Created by djek-grif on 1/8/17.
 */

public class SimpleSubscriber <T> extends Subscriber<T> {
    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        Logger.e(e, "Error of subscriber");
    }

    @Override
    public void onNext(T t) {}
}
