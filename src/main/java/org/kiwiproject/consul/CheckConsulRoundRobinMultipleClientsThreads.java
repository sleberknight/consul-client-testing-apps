package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import org.kiwiproject.consul.util.failover.strategy.RoundRobinConsulFailoverStrategy;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

/**
 * Checks behavior of the {@link RoundRobinConsulFailoverStrategy} with multiple threads using
 * raw threads and a {@link CountDownLatch}.
 */
@SuppressWarnings("all")
public class CheckConsulRoundRobinMultipleClientsThreads {
    public static void main(String[] args) throws InterruptedException {
        var targets = List.of(
                HostAndPort.fromString("a.foo.bar.baz:8500"),
                HostAndPort.fromString("b.foo.bar.baz:8500"),
                HostAndPort.fromString("c.foo.bar.baz:8500")
        );

        var delay = Duration.ofMillis(0L);
        var strategy = new RoundRobinConsulFailoverStrategy(targets, delay);

        var consul = Consul.builder()
                .withPing(false)
                .withHostAndPort(targets.get(0))  // without this, the first target will be the local agent
                .withFailoverInterceptorUsingStrategy(strategy)
                .build();

        var maxAttempts = 10;
        var delayMillisBetweenAttempts = 0;

        var numConcurrentReaders = 3;

        var latch = new CountDownLatch(numConcurrentReaders);

        for (var i = 0; i < numConcurrentReaders; i++) {
            final var readerNum = i + 1;
            new Thread(() -> {
                System.out.printf("Starting reader %d, will sleep %d milliseconds after failed attempts%n",
                        readerNum, delay.toMillis());
                var threadName = Thread.currentThread().getName();
                var description = String.format("reader %d, %s", readerNum, threadName);
                CheckConsul.check(description, consul, maxAttempts, delayMillisBetweenAttempts);
                latch.countDown();
            }).start();

            // introduce a bit of randomness when next client starts
            Thread.sleep(RandomGenerator.getDefault().nextInt(25, 50));
        }

        System.out.println("Wait for all readers to finish");
        latch.await(60, TimeUnit.SECONDS);
    }
}
