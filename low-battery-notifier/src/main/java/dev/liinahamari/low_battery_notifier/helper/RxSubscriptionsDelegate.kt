@file:Suppress("TooManyFunctions")
/*
 * Copyright 2022-2023 liinahamari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.liinahamari.low_battery_notifier.helper

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

internal interface RxSubscriptionsDelegate {
    fun <T : Any> Observable<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Observable<T>.subscribeUi(): Disposable

    fun <T : Any> Single<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Single<T>.subscribeUi(): Disposable

    fun <T : Any> Maybe<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable
    fun <T> Maybe<T>.subscribeUi(): Disposable

    fun <T : Any> Flowable<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Flowable<T>.subscribeUi(): Disposable

    fun Completable.subscribeUi(): Disposable

    fun <T : Any> Observable<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Observable<T>.addToDisposable(doOnSubscribe: () -> Unit): Disposable
    fun <T : Any> Observable<T>.addToDisposable(): Disposable

    fun <T : Any> Single<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Single<T>.addToDisposable(): Disposable

    fun <T : Any> Maybe<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable
    fun <T> Maybe<T>.addToDisposable(): Disposable

    fun <T : Any> Flowable<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable
    fun <T : Any> Flowable<T>.addToDisposable(): Disposable

    fun Completable.addToDisposable(): Disposable

    /** Must be called manually on lifecycle 'terminate' event */
    fun disposeSubscriptions()
}

internal class RxSubscriptionDelegateImpl : RxSubscriptionsDelegate {
    private val compositeDisposable = CompositeDisposable()

    override fun <T : Any> Single<T>.subscribeUi(): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
        .also(compositeDisposable::add)

    override fun <T> Maybe<T>.subscribeUi(): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Flowable<T>.subscribeUi(): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Observable<T>.subscribeUi(): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Observable<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable =
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(doOnSubscribe)
            .also(compositeDisposable::add)

    override fun <T : Any> Single<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(doOnSubscribe)
        .also(compositeDisposable::add)

    override fun <T : Any> Maybe<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(doOnSubscribe)
        .also(compositeDisposable::add)

    override fun <T : Any> Flowable<T>.subscribeUi(doOnSubscribe: Consumer<T>): Disposable =
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(doOnSubscribe)
            .also(compositeDisposable::add)

    override fun Completable.subscribeUi(): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Single<T>.addToDisposable(): Disposable = subscribe()
        .also(compositeDisposable::add)

    override fun <T> Maybe<T>.addToDisposable(): Disposable = subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Flowable<T>.addToDisposable(): Disposable = subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Observable<T>.addToDisposable(): Disposable = subscribe()
        .also(compositeDisposable::add)

    override fun <T : Any> Observable<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable =
        subscribe(doOnSubscribe)
            .also(compositeDisposable::add)

    override fun <T : Any> Observable<T>.addToDisposable(doOnSubscribe: () -> Unit): Disposable =
        subscribe { doOnSubscribe.invoke() }
            .also(compositeDisposable::add)

    override fun <T : Any> Single<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable = subscribe(doOnSubscribe)
        .also(compositeDisposable::add)

    override fun <T : Any> Maybe<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable = subscribe(doOnSubscribe)
        .also(compositeDisposable::add)

    override fun <T : Any> Flowable<T>.addToDisposable(doOnSubscribe: Consumer<T>): Disposable =
        subscribe(doOnSubscribe)
            .also(compositeDisposable::add)

    override fun Completable.addToDisposable(): Disposable = subscribe()
        .also(compositeDisposable::add)

    override fun disposeSubscriptions() = compositeDisposable.clear()
}
