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

import rx.cut.rxjava.Observable;
import rx.cut.rxjava.Observable.Operator;
import rx.cut.rxjava.Subscriber;
import rx.cut.rxjava.functions.Action0;

/**
 * This operator modifies an {@link Observable} so a given action is invoked when the {@link Observable} is subscribed.
 * @param <T> The type of the elements in the {@link Observable} that this operator modifies
 */
public class OperatorDoOnSubscribe<T> implements Operator<T, T> {
    private final Action0 subscribe;

    /**
     * Constructs an instance of the operator with the callback that gets invoked when the modified Observable is subscribed
     * @param subscribe the action that gets invoked when the modified {@link Observable} is subscribed
     */
    public OperatorDoOnSubscribe(Action0 subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        subscribe.call();
        // Pass through since this operator is for notification only, there is
        // no change to the stream whatsoever.
        return child;
    }
}
