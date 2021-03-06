/**
 * Copyright 2014 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.cut.rxjava.internal.operators;

import java.util.Iterator;

import rx.cut.rxjava.Observable.Operator;
import rx.cut.rxjava.Subscriber;
import rx.cut.rxjava.functions.Func2;
import rx.cut.rxjava.observers.Subscribers;

public final class OperatorZipIterable<T1, T2, R> implements Operator<R, T1> {

    final Iterable<? extends T2> iterable;
    final Func2<? super T1, ? super T2, ? extends R> zipFunction;

    public OperatorZipIterable(Iterable<? extends T2> iterable, Func2<? super T1, ? super T2, ? extends R> zipFunction) {
        this.iterable = iterable;
        this.zipFunction = zipFunction;
    }

    @Override
    public Subscriber<? super T1> call(final Subscriber<? super R> subscriber) {
        final Iterator<? extends T2> iterator = iterable.iterator();
        try {
            if (!iterator.hasNext()) {
                subscriber.onCompleted();
                return Subscribers.empty();
            }
        } catch (Throwable e) {
            subscriber.onError(e);
        }
        return new Subscriber<T1>(subscriber) {
            boolean once;
            @Override
            public void onCompleted() {
                if (once) {
                    return;
                }
                once = true;
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T1 t) {
                try {
                    subscriber.onNext(zipFunction.call(t, iterator.next()));
                    if (!iterator.hasNext()) {
                        onCompleted();
                    }
                } catch (Throwable e) {
                    onError(e);
                }
            }

        };
    }

}
