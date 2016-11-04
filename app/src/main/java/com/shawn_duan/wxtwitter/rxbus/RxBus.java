package com.shawn_duan.wxtwitter.rxbus;

import android.util.Log;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private static final String TAG = RxBus.class.getSimpleName();

    // rework on singleton pattern with double-checked locking
    private static volatile RxBus defaultInstance;
    private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

    public static RxBus getInstance() {
        RxBus rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new RxBus();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return mBusSubject.ofType(eventType);       // ofType == filter + cast
    }

//    public <T> Subscription register(final Class<T> eventType, Action1<T> onNext) {
//        return toObserverable(eventType)
//                .subscribe(onNext);
//    }

    public void post(Object event) {
        Log.d(TAG, "post event: " + event);
        mBusSubject.onNext(event);
    }
}