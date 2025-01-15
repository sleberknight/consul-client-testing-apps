package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import org.kiwiproject.consul.util.failover.strategy.RoundRobinConsulFailoverStrategy;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

/**
 * Checks behavior of the {@link RoundRobinConsulFailoverStrategy} with multiple threads using a
 * fixed-thread pool and a {@link CompletionService}.
 */
@SuppressWarnings("all")
public class CheckConsulRoundRobinMultipleClientsExecutor {
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

        var threadPool = Executors.newFixedThreadPool(numConcurrentReaders);
        var completionService = new ExecutorCompletionService<>(threadPool);

        for (var i = 0; i < numConcurrentReaders; i++) {
            final var readerNum = i + 1;
            completionService.submit(() -> {
                System.out.println("Starting reader " + readerNum);
                var threadName = Thread.currentThread().getName();
                var description = String.format("reader %d, %s", readerNum, threadName);
                CheckConsul.check(description, consul, maxAttempts, delayMillisBetweenAttempts);
                return null;
            });

            // introduce a bit of randomness when next client starts
            Thread.sleep(RandomGenerator.getDefault().nextInt(50, 150));
        }

        System.out.println("Wait for all readers to finish");
        for (var i = 0; i < numConcurrentReaders; i ++) {
            completionService.take();
        }

        threadPool.shutdown();  // shutdown, or else it hangs forever...
    }
}
