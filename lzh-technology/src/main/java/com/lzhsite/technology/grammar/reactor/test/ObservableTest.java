package com.lzhsite.technology.grammar.reactor.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.google.common.base.Splitter;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.exceptions.Exceptions;
import rx.internal.operators.OperatorDistinct;
import rx.observables.AsyncOnSubscribe;
import rx.observables.ConnectableObservable;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 上午10:19
 * @since 2017/12/26
 */
public class ObservableTest {

    @Test
    public void test_14(){
        int max = 100;
        CountDownLatch latch = new CountDownLatch(1);
        Observable.range(1, max)
                .observeOn((Scheduler) Schedulers.immediate())
                .subscribeOn(Schedulers.computation())
                .doOnNext(i ->{
                    if(i == 95) {
                        throw new RuntimeException("this error value.");
                    }
                    System.out.println("next " + Thread.currentThread() + " : "  + i);
                })
                .filter(i -> i > 90)
                .subscribeOn(Schedulers.io())
//                .onBackpressureBuffer(10)
//                .toBlocking()
                .subscribe(i -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1,100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("subs " + Thread.currentThread() + " : "  + i);

                }, t -> {
                    System.out.println("subs error : " + t);
                    latch.countDown();
                }, latch::countDown);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_33(){
        Observable.range(1, 5)
                .doOnNext(i ->{
                    if(i == 3) {
                        throw new RuntimeException("this value == 3.");
                    }
                    System.out.println("next " + Thread.currentThread() + " : "  + i);
                })
//                .onErrorReturn(throwable -> {
//                    System.out.println("subs re error : " + throwable);
//                    return -1;
//                })
//                .onExceptionResumeNext(Observable.just(-1))
//                .onErrorResumeNext(Observable.just(-1))
//                .doOnError(t -> {
//                    System.out.println("subs do error : " + t);
//                })
                .subscribe(i -> {
                    System.out.println("subs " + Thread.currentThread() + " : "  + i);
                }, throwable -> {
                    System.out.println("subs error : " + throwable);
                }, () -> {
                    System.out.println("subs completed.");
                });
    }

    @Test
    public void test_91(){
        final CountDownLatch latch = new CountDownLatch(1);
        Observable.range(1, 1000)
//                .onBackpressureBuffer(10)
                .throttleLast(1, TimeUnit.MILLISECONDS)
//                .window(10)
                .subscribe(i -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(10,100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("subs " + Thread.currentThread() + " : "  + i + ", time : " + System.currentTimeMillis());
                }, t -> {
                    latch.countDown();
                }, () -> {
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(" " + e.getMessage());
        }
    }
    @Test
    public void test_batch(){
        final CountDownLatch latch = new CountDownLatch(1);
        Observable.range(1, 1000)
                .subscribeOn(Schedulers.io())
                .buffer(10, TimeUnit.MILLISECONDS)
                .debounce(12, TimeUnit.MILLISECONDS)
                .subscribe(list -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(10,100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("---------------");
                    for (Integer i : list)
                        System.out.println("subs " + Thread.currentThread() + " : "  + i + ", time : " + System.currentTimeMillis());
                }, t -> {
                    latch.countDown();
                }, () -> {
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    @Test
    public void test_windown(){
        final CountDownLatch latch = new CountDownLatch(1);
        Observable.range(1, 1000)
                .subscribeOn(Schedulers.computation())
                .window(10)
                .subscribe(listObservable -> {
                    listObservable.subscribe(i -> {
                        System.out.println("subs " + Thread.currentThread() + " : "  + i + ", time : " + System.currentTimeMillis());
                    });
                }, t -> {
                    latch.countDown();
                }, () -> {
                    latch.countDown();
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    @Test
    public void test_sub(){
        Observable.range(1, 10).zipWith(Observable.range(100, 200), (a, b) -> a+b).subscribe(System.out::println);

        ConnectableObservable<Integer> o = Observable.range(1, 10).publish();
        o.connect();
        o.subscribe(System.out::println);
        o.subscribe(System.out::println);

        try {
            Thread.sleep(1111);
            System.out.println("-----");
            o.subscribe(System.out::println);
            o.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("---xxxxx");
        Observable<Integer> o2 = Observable.range(1, 10).publish().refCount();
        o2.subscribe(System.out::println);
        o2.subscribe(System.out::println);

    }
    @Test
    public void test_194(){
        Observable.range(1, 10)
                .doAfterTerminate(() -> System.out.println("------- doAfterTerminate"))
                .subscribe(System.out::println);
    }
    @Test
    public void test_200(){
        Observable.from(Arrays.asList(1,3,2,4,5,3,2))
                .lift(OperatorDistinct.<Integer>instance())
                .doAfterTerminate(() -> System.out.println("------- doAfterTerminate"))
                .subscribe(System.out::println);
    }
    @Test
    public void test_61() throws IOException {
        //.wq p
        Document doc = Jsoup.connect("http://www.18ladys.com/cyzy/zy292.html").timeout(38000).get();
        Elements es = doc.select(".wq p");
        System.out.println(es.text());
        System.out.println("------>>>");
        System.out.println();
        Iterable<String> it = Splitter.on("<br>").trimResults().split(es.html());



    }
    
    
    @Test
    public void test_220(){

        final OkHttpClient okHttp = new OkHttpClient.Builder().build();
        final Request request = new Request.Builder().get().url("http://www.baidu.com").build();
        Observable.create(new SyncOnSubscribe<Call, Response>() {
            @Override
            protected Call generateState() {
                return okHttp.newCall(request);
            }
            @Override
            protected Call next(Call state, Observer<? super Response> observer) {
                if (state.isCanceled() || state.isExecuted()) {
                    observer.onCompleted();
                    return state;
                }
                try {
                    Response response = state.execute();
                    observer.onNext(response);
                } catch (IOException e) {
                    Exceptions.throwOrReport(e, observer);
                }
                observer.onCompleted();
                return state;
            }

            @Override
            protected void onUnsubscribe(Call state) {
                state.cancel();
            }
        }).subscribe(response -> {
            System.out.println(response.isSuccessful());
            try {
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    @Test
    public void test_zip(){
        final OkHttpClient okHttp = new OkHttpClient.Builder().build();
        final Request request = new Request.Builder().get().url("http://www.baidu.com").build();
        Observable<Response> observable1 = executeResponse(okHttp, request);
        Observable<Response> observable2 = executeResponse(okHttp, request);
//        observable1.subscribe(response -> {
//            System.out.println(response.isSuccessful());
//            try {
//                System.out.println(response.body().string());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        Observable.zip(observable1, observable2, (response, response2) -> {
            List<Response> list = new ArrayList<Response>(8);
            list.add(response);
            list.add(response2);
            return list;
        }).subscribe();
    }

    Observable<Response> executeResponse(final OkHttpClient okHttp, final Request request) {
        return Observable.<Response>create(new AsyncOnSubscribe<Call, Response>() {
            @Override
            protected Call generateState() {
                return okHttp.newCall(request);
            }
            @Override
            protected Call next(Call state, long requested, Observer<Observable<? extends Response>> observer) {
                if (state.isCanceled() || state.isExecuted()) {
                    observer.onCompleted();
                    return state;
                }
                observer.onNext(Observable.create(subscriber -> {
                    try {
                        Response response = state.execute();
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }));
//                try {
//                    Response response = state.execute();
//                    observer.onNext(Observable.just(response));
////                    observer.onNext(Observable.fromCallable(state::execute).subscribeOn(Schedulers.io()));
//                } catch (Exception e) {
//                    Exceptions.throwOrReport(e, observer);
//                }
                observer.onCompleted();
                return state;
            }

            @Override
            protected void onUnsubscribe(Call state) {
                state.cancel();
            }
        });
    }
}
