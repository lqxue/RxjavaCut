/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rx.cut.rxjava.internal.schedulers;

import rx.cut.rxjava.Scheduler;
import rx.cut.rxjava.Subscription;
import rx.cut.rxjava.functions.Action0;
import rx.cut.rxjava.plugins.RxJavaPlugins;
import rx.cut.rxjava.plugins.RxJavaSchedulersHook;
import rx.cut.rxjava.subscriptions.Subscriptions;

import java.util.concurrent.*;

/**
 * @warn class description missing
 */
public class NewThreadWorker extends Scheduler.Worker implements Subscription {
    private final ScheduledExecutorService executor;
    private final RxJavaSchedulersHook schedulersHook;
    volatile boolean isUnsubscribed;

    /* package */
    public NewThreadWorker(ThreadFactory threadFactory) {
        executor = Executors.newScheduledThreadPool(1, threadFactory);
        schedulersHook = RxJavaPlugins.getInstance().getSchedulersHook();
    }

    @Override
    public Subscription schedule(final Action0 action) {
        return schedule(action, 0, null);
    }

    @Override
    public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
        if (isUnsubscribed) {
            return Subscriptions.empty();
        }
        return scheduleActual(action, delayTime, unit);
    }

    /**
     * @warn javadoc missing
     * @param action
     * @param delayTime
     * @param unit
     * @return
     */
    public ScheduledAction scheduleActual(final Action0 action, long delayTime, TimeUnit unit) {
        Action0 decoratedAction = schedulersHook.onSchedule(action);
        ScheduledAction run = new ScheduledAction(decoratedAction);
        Future<?> f;
        if (delayTime <= 0) {
            f = executor.submit(run);
        } else {
            f = executor.schedule(run, delayTime, unit);
        }
        run.add(Subscriptions.from(f));

        return run;
    }

    @Override
    public void unsubscribe() {
        isUnsubscribed = true;
        executor.shutdownNow();
    }

    @Override
    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }
}
