package com.lzhsite.technology.grammar.reactor.test;

import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.ORDER_Q_2;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.ORDER_Q_4;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.ORDER_Q_7;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.ORDER_Q_ABNORMAL_MARK_CHANGE;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.ORDER_Q_PAY_END;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.Q_APPOINT;
import static com.lzhsite.technology.grammar.reactor.test.RxFileTest.RoutKey.Q_FEED;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

 

/**
 * <p>reactorDome
 * <p>com.lzhsite.technology.grammar.reactor.test
 *
 * @author stony
 * @version 上午11:28
 * @since 2018/1/8
 */
public class RxFileTest {

    final String dir8010 = "/yongche/backlogs/server5/t8010/";
    final String dir8080 = "/yongche/backlogs/server5/t8080/";
    final String fileName = "operation_20170402.log";

    @Test
    public void test_rxjava_simple() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicLong index = new AtomicLong();
        final long startTime = System.currentTimeMillis();
        final File logFile = new File(dir8010 + fileName);
        Observable
                .create(new LogFilePublisher(logFile))
                .subscribeOn(Schedulers.computation())
                .map(new StreamEventTransFrom())
                .filter(new StreamEventFilter())
                .subscribeOn(Schedulers.io())
                .subscribe();
        latch.await();
        System.out.println("finish index : " + index.get());
    }

    @Test
    public void test_rxjava_call() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final long startTime = System.currentTimeMillis();
        final AtomicLong index = new AtomicLong();
        final StreamEventDispatch dispatch = new StreamEventDispatch();
        final File logFile = new File(dir8010 + fileName);
        Observable
                .create(new LogFilePublisher(logFile))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new StreamEventTransFrom())
                .filter(new StreamEventFilter())
                .flatMap(new CallableConsumer(dispatch))
                .window(1, TimeUnit.SECONDS)
                .flatMap(Observable::count)
                .subscribe();
        latch.await();
        System.out.println("finish index : " + index.get());
    }
    @Test
    public void test_rxjava_group() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final long startTime = System.currentTimeMillis();
        final AtomicLong index = new AtomicLong();
        final StreamEventDispatch dispatch = new StreamEventDispatch();
        final File logFile = new File(dir8010 + fileName);
        Observable
                .create(new LogFilePublisher(logFile))
                .subscribeOn(Schedulers.io())
                .map(new StreamEventTransFrom())
                .filter(new StreamEventFilter())
                .groupBy(streamEvent -> streamEvent.type.type)
                .flatMap(new GroupConsumer(dispatch))
//                .subscribeOn(Schedulers.computation())
                .window(1, TimeUnit.SECONDS)
                .flatMap(Observable::count)
                .subscribe();
        latch.await();
        System.out.println("finish index : " + index.get());
    }

    /**
     * 异步处理事件
     */
    class CallableConsumer implements Func1<StreamEvent, Observable<StreamEvent>> {
        final StreamEventDispatch dispatch;
        public CallableConsumer(StreamEventDispatch dispatch) {
            this.dispatch = dispatch;
        }
        @Override
        public Observable<StreamEvent> call(final StreamEvent event) {
            return Observable.fromCallable(() -> {
                event.done = true;
                dispatch.doWork(event);
                return event;
            }).subscribeOn(Schedulers.io());
//            return Observable.just(event).observeOn(Schedulers.io()).map(event1 -> {
//                event1.done = true;
//                dispatch.doWork(event1);
//                return event1;
//            });
        }
    }

    /**
     * 分组异步处理
     */
    class GroupConsumer implements Func1<GroupedObservable<Integer, StreamEvent>, Observable<StreamEvent>> {
        final StreamEventDispatch dispatch;
        public GroupConsumer(StreamEventDispatch dispatch) {
            this.dispatch = dispatch;
        }
        @Override
        public Observable<StreamEvent> call(GroupedObservable<Integer, StreamEvent> group) {
            return group.observeOn(Schedulers.io()).flatMap(streamEvent -> {
//                return Observable.fromCallable(() -> {
//                    streamEvent.done = true;
//                    dispatch.doWork(streamEvent);
//                    return streamEvent;
//                }).subscribeOn(Schedulers.io());
                return Observable.just(streamEvent).observeOn(Schedulers.io()).map(event1 -> {
                    event1.done = true;
                    dispatch.doWork(event1);
                    return event1;
                });
            });
//            return group.observeOn(Schedulers.io()).map(new Func1<StreamEvent, StreamEvent>() {
//                @Override
//                public StreamEvent call(final StreamEvent event) {
////                    System.out.println("process event : " + event);
//                    event.done = true;
//                    dispatch.doWork(event);
//                    return event;
//                }
//            });
        }
    }

    /**
     * 事件过滤
     */
    class StreamEventFilter implements Func1<StreamEvent, Boolean> {
        @Override
        public Boolean call(StreamEvent streamEvent) {
            return streamEvent.type != StreamEventTypeEnum.NONE;
        }
    }
    /**
     * 事件转换
     */
    class StreamEventTransFrom implements Func1<String, StreamEvent> {
        @Override
        public StreamEvent call(String line) {
            if (line.contains("AppointWorker")) {
                if (line.contains(Q_APPOINT)) {
                    return new StreamEvent(StreamEventTypeEnum.AppointWorker, Q_APPOINT, extractMsgAndTransform(Q_APPOINT, line));
                }
            } else if (line.contains("DoubtAndChangeWorker")) {
                if (line.contains(RoutKey.ORDER_Q_7)) {
                    return new StreamEvent(StreamEventTypeEnum.DoubtAndChangeWorker, ORDER_Q_7, extractMsgAndTransform(ORDER_Q_7, line));
                } else if (line.contains(RoutKey.ORDER_Q_2)) {
                    return new StreamEvent(StreamEventTypeEnum.DoubtAndChangeWorker, ORDER_Q_2, extractMsgAndTransform(ORDER_Q_2, line));
                } else if (line.contains(RoutKey.ORDER_Q_4)) {
                    return new StreamEvent(StreamEventTypeEnum.DoubtAndChangeWorker, ORDER_Q_4, extractMsgAndTransform(ORDER_Q_4, line));
                } else if (line.contains(RoutKey.ORDER_Q_ABNORMAL_MARK_CHANGE)) {
                    return new StreamEvent(StreamEventTypeEnum.DoubtAndChangeWorker, ORDER_Q_ABNORMAL_MARK_CHANGE, extractMsgAndTransform(ORDER_Q_ABNORMAL_MARK_CHANGE, line));
                }
            } else if (line.contains("PayedOrderWorker")) {
                if (line.contains(RoutKey.ORDER_Q_PAY_END)) {
                    return new StreamEvent(StreamEventTypeEnum.PayedOrderWorker, ORDER_Q_PAY_END, extractMsgAndTransform(ORDER_Q_PAY_END, line));
                }
            } else if (line.contains("CommentWorker")) {
                if (line.contains(Q_FEED)) {
                    return new StreamEvent(StreamEventTypeEnum.CommentWorker, Q_FEED, extractMsgAndTransform(Q_FEED, line));
                }
            } else if (line.contains("RiskWorker")) {
                return new StreamEvent(StreamEventTypeEnum.RiskWorker, "", extractMsgAndTransform(line));
            }
            return new StreamEvent(StreamEventTypeEnum.NONE, null, null);
        }
    }

    /**
     * 事件发布
     */
    class LogFilePublisher implements Observable.OnSubscribe<String> {
        final File logFile;
        final AtomicLong index = new AtomicLong();

        LogFilePublisher(File logFile) {
            this.logFile = logFile;
        }

   
		@Override
		public void call(rx.Subscriber<? super String> publisher) {
			// TODO Auto-generated method stub
		    if (logFile == null || !logFile.exists()) {
                publisher.onError(new NullPointerException("The logFile is null or not exists."));
                return;
            }
            try {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.length() > 0) {
                            publisher.onNext(line);
                            index.incrementAndGet();
                        }
                    }
                } finally {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    System.out.println("----->>>  publisher completed.");
                    System.out.println("reader index : " + index.get());
                    publisher.onCompleted();
                }
            } catch (IOException e) {
                publisher.onError(e);
            }
		}
    }

    class StreamEvent {
        StreamEventTypeEnum type;
        //AppointWorker, DoubtAndChangeWorker , PayedOrderWorker,CommentWorker,RiskWorker
        String routeKey;
        byte[] msg;
        boolean done;

        public StreamEvent(StreamEventTypeEnum type, String routeKey, byte[] msg) {
            this.type = type;
            this.routeKey = routeKey;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "StreamEvent{" +
                    "type=" + type +
                    ", routeKey='" + routeKey + '\'' +
                    ", msg=" + msg.length +
                    ", done=" + done +
                    '}';
        }
    }

    enum StreamEventTypeEnum {
        AppointWorker(1, "AppointWorker"),
        DoubtAndChangeWorker(2, "DoubtAndChangeWorker"),
        PayedOrderWorker(3, "PayedOrderWorker"),
        CommentWorker(4, "CommentWorker"),
        RiskWorker(5, "RiskWorker"),
        NONE(-1, null);
        int type;
        String name;

        StreamEventTypeEnum(int type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return "{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * 事件分发
     */
    class StreamEventDispatch {
        void doWork(StreamEvent event) {
//            System.out.println("dispatch event : " + event);
            if (event.type == StreamEventTypeEnum.AppointWorker) {
                appointWorker.doWork(event.routeKey, event.msg);
            } else if (event.type == StreamEventTypeEnum.DoubtAndChangeWorker) {
                doubtAndChangeWorker.doWork(event.routeKey, event.msg);
            } else if (event.type == StreamEventTypeEnum.PayedOrderWorker) {
                payedOrderWorker.doWork(event.routeKey, event.msg);
            } else if (event.type == StreamEventTypeEnum.CommentWorker) {
                commentWorker.doWork(event.routeKey, event.msg);
            } else if (event.type == StreamEventTypeEnum.RiskWorker) {
                riskWorker.doWork(event.routeKey, event.msg);
            }
        }
    }
    BaseWork appointWorker = new BaseWork() {
        @Override
        public void doWork(String routerKey, byte[] msg) {
//            System.out.println("AppointWorker--->" + routerKey + "|" + new String(msg, CHARSET_UTF8));
            doSomething();
        }
    };
    BaseWork doubtAndChangeWorker = new BaseWork() {
        @Override
        public void doWork(String routerKey, byte[] msg) {
//            System.out.println("DoubtAndChangeWorker--->" + routerKey + "|" + new String(msg, CHARSET_UTF8));
            doSomething();
        }
    };
    BaseWork payedOrderWorker = new BaseWork() {
        @Override
        public void doWork(String routerKey, byte[] msg) {
//            System.out.println("PayedOrderWorker--->" + routerKey + "|" + new String(msg, CHARSET_UTF8));
            doSomething();
        }
    };
    BaseWork commentWorker = new BaseWork() {
        @Override
        public void doWork(String routerKey, byte[] msg) {
//            System.out.println("CommentWorker--->" + routerKey + "|" + new String(msg, CHARSET_UTF8));
            doSomething();
        }
    };
    BaseWork riskWorker = new BaseWork() {
        @Override
        public void doWork(String routerKey, byte[] msg) {
//            System.out.println("RiskWorker--->" + routerKey + "|" + new String(msg, CHARSET_UTF8));
            doSomething();
        }
    };
    interface BaseWork {
        void doWork(String routerKey, byte[] msg);
        default void doSomething(){
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("doSomething error.");
            }
        }
    }

    protected Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    protected byte[] transform(String msg){
        return msg.getBytes(CHARSET_UTF8);
    }
    protected String extractMsg(String key, String line){
        return line.split(key+"\\|")[1];
    }
    protected String extractMsg(String line){
        return line.split("\\|")[1];
    }

    protected byte[] extractMsgAndTransform(String key, String line){
        return transform(extractMsg(key,line));
    }
    protected byte[] extractMsgAndTransform(String line){
        return transform(extractMsg(line));
    }


    class RoutKey {
        public static final String ORDER_Q_2 = "order_q_2";
        public static final String ORDER_Q_4 = "order_q_4";
        public static final String ORDER_Q_7 = "order_q_7";
        public static final String ORDER_Q_PAY_END = "order_q_pay_end";
        public static final String ORDER_Q_ABNORMAL_MARK_CHANGE = "order_q_abnormal_mark_change";
        public static final String Q_APPOINT = "chelv_appoint";
        public static final String Q_FEED = "feed.key.evaluation";
    }
}
