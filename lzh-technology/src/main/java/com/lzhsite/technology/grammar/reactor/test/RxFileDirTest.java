package com.lzhsite.technology.grammar.reactor.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import rx.functions.Func1;
import rx.schedulers.Schedulers;

 

/**
 * <p>reactorDome
 * <p>com.lzhsite.technology.grammar.reactor.test
 *
 * @author stony
 * @version 上午11:18
 * @since 2018/1/8
 */
public class RxFileDirTest {

    @Test
    public void test_14() throws Exception {
        LocalDate date = LocalDate.of(2017, 4, 2);
        DateTimeFormatter format = DateTimeFormatter.BASIC_ISO_DATE; //.ofPattern("yyyyMMdd");
        final String fileName = "operation_" + date.format(format) + ".log";
        System.out.println(fileName);

        final List<File> processFiles = Files.find(Paths.get("/yongche/backlogs"), 5, (path, basicFileAttributes) -> !basicFileAttributes.isDirectory())
                .filter(path -> fileName.equals(path.getFileName().toString()))
                .map(Path::toFile)
                .collect(Collectors.toList());
        System.out.println("processFiles size : " + processFiles.size());
        final CountDownLatch latch = new CountDownLatch(processFiles.size());
        final CountDownLatch finish = new CountDownLatch(1);

        rx.Observable.from(processFiles).flatMap(new Func1<File, rx.Observable<String>>() {
            @Override
            public rx.Observable<String> call(final File file) {
//                return Observable.fromCallable(new Callable<String>() {
//                    @Override
//                    public String call() throws Exception {
//                        return processLogFile(file, latch);
//                    }
//                }).subscribeOn(Schedulers.io());
                return rx.Observable.just(file).observeOn(Schedulers.io()).map(new Func1<File, String>() {
                    @Override
                    public String call(final File file) {
                        return processLogFile(file, latch);
                    }
                });
            }
        }).subscribe();
        finish.await();
        System.out.println("finish ok");
        latch.await();
        System.out.println("latch ok");
    }

    String processLogFile(final File file, final CountDownLatch latch) {
        final AtomicLong index = new AtomicLong();
        rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
            final File logFile = file;
            
            public void call(Subscriber<? super String> subscriber) {
                if (!logFile.exists()) {
                    subscriber.onComplete();
                    return;
                }
                try {
                    Files.lines(Paths.get(file.toURI()), StandardCharsets.UTF_8)
                            .filter(line -> line.length() > 0)
                            .forEach(subscriber::onNext);
                    System.out.println("----->>>  publisher completed.");
                    subscriber.onComplete();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
			@Override
			public void call(rx.Subscriber<? super String> t) {
				// TODO Auto-generated method stub
				
			}
        }).subscribeOn(Schedulers.io()).subscribe();
        return file.getAbsolutePath();

    }

    @Test
    public void test_148() throws IOException {
        LocalDate date = LocalDate.of(2017, 4, 2);
        LocalDateTime localDateTime = LocalDateTime.of(2017, 4, 2, 10, 10);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter format2 = DateTimeFormatter.BASIC_ISO_DATE;
        System.out.println(localDateTime.format(format));
        System.out.println(localDateTime.format(format2));
        System.out.println(date.format(format));
        System.out.println(date.format(format2));
        System.out.println(new File("/yongche/backlogs").toURI());
        System.out.println(new File("/yongche/backlogs").toURI().getScheme());

        final String fileName = "operation_" + date.format(format) + ".log";
        System.out.println(fileName);
        System.out.println("----------");
        Files.list(Paths.get("/yongche/backlogs")).forEach(path -> System.out.println( path.getFileName()));
        System.out.println("---------");
        Files.find(Paths.get("/yongche/backlogs"), 5, new BiPredicate<Path, BasicFileAttributes>() {
            @Override
            public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
                return !basicFileAttributes.isDirectory();
            }
        }).filter(path -> fileName.equals(path.getFileName().toString()))
                .forEach(path -> {
                    System.out.println( path.getFileName());
                    System.out.println( path.toFile());
                });


        System.out.println("---------------------------");
        List<File> files = Files.find(Paths.get("/yongche/backlogs"), 5, (path, basicFileAttributes) -> !basicFileAttributes.isDirectory())
                .filter(path -> fileName.equals(path.getFileName().toString()))
                .map(Path::toFile)
                .collect(Collectors.toList());

        System.out.println(files);


    }
}
