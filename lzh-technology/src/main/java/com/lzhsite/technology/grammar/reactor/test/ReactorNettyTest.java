package com.lzhsite.technology.grammar.reactor.test;
//import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.util.SocketUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.http.server.HttpServerRequest;
import reactor.ipc.netty.http.server.HttpServerResponse;
import reactor.ipc.netty.http.server.HttpServerRoutes;
import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

/**
 * <p>reactorDome
 * <p>com.lzhsite.technology.grammar.reactor.test
 *
 * @author stony
 * @version 下午6:37
 * @since 2018/1/8
 */
public class ReactorNettyTest {

    @Test
    public void test_14() throws URISyntaxException {
        Path resource = Paths.get(this.getClass().getResource("/public").toURI());

        NettyContext context = HttpServer.create(8080)
                .newRouter(routes -> {
                    routes.get("/get", (req, resp) -> resp.status(200).sendString(Mono.just("" + ThreadLocalRandom.current().nextInt(10, 20))))
                            .post("/v1/api/{param}", (req, res) -> Mono.empty())
                            .post("/v2/api/{param}", (req, res) ->
                                    res.sendString(req.receive()
                                            .asString()
                                            .log("server-received")
                                            .map(it -> it + ' ' + req.param("param") + '!')
                                            .log("server-reply")))
                            .directory("/test", resource);
                }).block();


        HttpClientResponse response = HttpClient.create(context.address()
                .getPort())
                .get("/get")
                .block(Duration.ofMillis(1000));
        System.out.println("response = " + response);

        HttpClientResponse response0 = HttpClient.create(context.address()
                .getPort())
                .get("/test/index.html")
                .block(Duration.ofSeconds(30));

        Mono<Tuple2<HttpHeaders, String>> response3 =
                HttpClient.create(ops -> ops.connectAddress(context::address))
                        .request(HttpMethod.GET, "/test/index.html", HttpClientRequest::send)
                        .flatMap(res -> Mono.zip(Mono.just(res.responseHeaders()),
                                res.receive()
                                        .aggregate()
                                        .asString()
                                        .defaultIfEmpty("NO BODY")));

        HttpClient client = HttpClient.create("localhost", context.address().getPort());

        Mono<ByteBuf> content = client
                .post("/v1/api/World", req -> req.header("Content-Type", "text/plain")
                        .sendString(Mono.just("Hello")
                                .log("client-send")))
                .flatMap(res -> res.receive()
                        .log("client-received")
                        .next())
                .doOnError(t -> System.err.println("Failed requesting server: " + t.getMessage()));

        Mono<String> content2 =
                client.post("/v2/api/World", req -> req.header("Content-Type", "text/plain")
                        .sendString(Flux.just("Hello")
                                .log("client-send")))
                        .flatMap(res -> res.receive()
                                .aggregate()
                                .asString()
                                .log("client-received"))
                        .doOnError(t -> System.err.println("Failed requesting server: " + t.getMessage()));

        System.out.println(response.receive().aggregate().asString(StandardCharsets.UTF_8).block());
        System.out.println(response0.receive().aggregate().asString(StandardCharsets.UTF_8).block());
        System.out.println(response3.block(Duration.ofSeconds(30)));

        StepVerifier.create(content)
                .expectComplete()
                .verify(Duration.ofSeconds(5000));

        StepVerifier.create(content2)
                .expectNextMatches(s -> s.equals("Hello World!"))
                .expectComplete()
                .verify(Duration.ofSeconds(5000));
        context.dispose();
    }


    @Test
    public void test_45() {
        HttpServer.Builder builder = HttpServer.builder()
                .options(o -> o.port(9081))
                .port(9080);
        HttpServer binding = builder.build();
        BlockingNettyContext blockingFacade = binding.start((req, resp) -> resp.sendNotFound());
        blockingFacade.installShutdownHook();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            blockingFacade.shutdown();
        }
    }

    @Test
    public void test_ssl() throws URISyntaxException, SSLException, CertificateException {
//        File certChainFile = Paths.get(this.getClass().getResource("/ssl/server.crt").toURI()).toFile();
//        File keyFile = Paths.get(this.getClass().getResource("/ssl/server.key").toURI()).toFile();
//        SslContext sslContext = SslContextBuilder.forServer(certChainFile, keyFile).sslProvider(SslProvider.OPENSSL_REFCNT).build();

        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslServer = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        SslContext sslClient = SslContextBuilder.forClient().trustManager(ssc.cert()).build();

        CountDownLatch latch = new CountDownLatch(1);
        NettyContext context =
                HttpServer.create(opt -> opt.sslContext(sslServer).port(8082))
                        .newRouter(new Consumer<HttpServerRoutes>() {
                            @Override
                            public void accept(HttpServerRoutes routes) {
                                routes.post("/post", (req, resp) -> resp.status(200).sendString(Mono.just("post")));
                                routes.get("/get", (req, resp) -> resp.status(200).sendString(Mono.just("hello")));
                            }
                        })
                        .block();

        try {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            context.dispose();
            context.onClose().block();
        }
    }
    @Test
    public void sendFileSecure() throws URISyntaxException, CertificateException, SSLException {
        Path largeFile = Paths.get(this.getClass().getResource("/files/largeFile.txt").toURI());
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslServer = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        SslContext sslClient = SslContextBuilder.forClient().trustManager(ssc.cert()).build();

        NettyContext context =
                HttpServer.create(opt -> opt.sslContext(sslServer).port(8080))
                        .newHandler((req, resp) -> resp.sendFile(largeFile))
                        .block();

        System.out.println("address = " + context.address());


        HttpClientResponse response =
                HttpClient.create(opt -> opt.port(context.address().getPort())
                        .sslContext(sslClient))
                        .get("/foo")
                        .block(Duration.ofSeconds(120));

        context.dispose();
        context.onClose().block();
     
        String body = response.receive().aggregate().asString(StandardCharsets.UTF_8).block();
        System.out.println(">>> body: \n" + body);
//        assertThat(body)
//                .startsWith("This is an UTF-8 file that is larger than 1024 bytes.")
//                .contains("1024 mark here -><- 1024 mark here")
//                .endsWith("End of File");
    }

    @Test
    public void sendFileAsync() throws IOException, URISyntaxException {
        Path largeFile = Paths.get(getClass().getResource("/files/largeFile.txt").toURI());
        Path tempFile = Files.createTempFile(largeFile.getParent(), "temp", ".txt");
        tempFile.toFile().deleteOnExit();
        //将 largeFile 内容写入 tempFile
        byte[] fileBytes = Files.readAllBytes(largeFile);
        for (int i = 0; i < 1000; i++) {
            Files.write(tempFile, fileBytes, StandardOpenOption.APPEND);
        }
        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        //aio
        AsynchronousFileChannel channel =
                AsynchronousFileChannel.open(tempFile, StandardOpenOption.READ);
        Flux<ByteBuf> content =  Flux.create(fluxSink -> {
            fluxSink.onDispose(() ->{
                try {
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException ignored) {}
            });
            //  分配4M空间
            ByteBuffer buf = ByteBuffer.allocate(4096);
            channel.read(buf, 0, buf, new ReadFileCompletionHandler(channel, fluxSink, allocator));
        });
        //接受上传内容后写出
        NettyContext context =
                HttpServer.create(opt -> opt.host("localhost").port(8080))
                        .newHandler(new BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>>() {
                            @Override
                            public Publisher<Void> apply(HttpServerRequest req, HttpServerResponse resp) {
                                return resp.sendByteArray(req.receive().aggregate().asByteArray());
                            }
                        })
                        .block(Duration.ofSeconds(30));

        byte[] response = HttpClient.create(opt -> opt.connectAddress(context::address))
            .request(HttpMethod.POST,"/", req -> req.send(content).then())
            .flatMap(res -> res.receive().aggregate().asByteArray())
            .block(Duration.ofSeconds(30));

       // assertThat(response).isEqualTo(Files.readAllBytes(tempFile));
        context.dispose();
    }
    static final class ReadFileCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousFileChannel channel;
        private final FluxSink<ByteBuf> sink;
        private final ByteBufAllocator allocator;
        private AtomicLong position;
        ReadFileCompletionHandler(AsynchronousFileChannel channel, FluxSink<ByteBuf> sink, ByteBufAllocator allocator) {
            this.channel = channel;
            this.sink = sink;
            this.allocator = allocator;
            this.position = new AtomicLong(0);
        }
        @Override
        public void completed(Integer read, ByteBuffer dataBuffer) {
            if (read != -1) {
                long pos = this.position.addAndGet(read);
                //设置读取缓冲区数据 position-limit
                dataBuffer.flip();
                ByteBuf buf = allocator.buffer().writeBytes(dataBuffer);
                this.sink.next(buf);
                if (!this.sink.isCancelled()) {
                    ByteBuffer newByteBuffer = ByteBuffer.allocate(4096);
                    this.channel.read(newByteBuffer, pos, newByteBuffer, this);
                }
            } else {
                try {
                    if (channel != null) {
                        channel.close();
                    }
                } catch (IOException ignored) {}
                this.sink.complete();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer dataBuffer) {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException ignored) {}
            this.sink.error(exc);
        }
    }

    @Test
    public void test_tcp(){
        CountDownLatch latch = new CountDownLatch(10);
        int port = SocketUtils.findAvailableTcpPort(10000, 19999);
        ObjectMapper m = new ObjectMapper();

    }



}
