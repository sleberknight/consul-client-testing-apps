package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import org.kiwiproject.consul.util.failover.strategy.ConsulFailoverStrategy;
import org.kiwiproject.consul.util.failover.strategy.RoundRobinConsulFailoverStrategy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Checks behavior of the {@link RoundRobinConsulFailoverStrategy} using a single thread.
 */
@SuppressWarnings("all")
public class CheckConsulRoundRobin {
    public static void main(String[] args) {
        var targets = List.of(
                HostAndPort.fromString("a.foo.bar.baz:8500"),
                HostAndPort.fromString("b.foo.bar.baz:8500"),
                HostAndPort.fromString("c.foo.bar.baz:8500")
        );

        var delay = Duration.ofMillis(25L);
        var strategy = new RoundRobinConsulFailoverStrategy(targets, delay);

        var consul = Consul.builder()
                .withPing(false)
                .withHostAndPort(targets.get(0))  // without this, the first target will be the local agent
                .withFailoverInterceptorUsingStrategy(strategy)
                .build();

        var maxAttempts = 10;
        var delayMillisBetweenAttempts = 500;
        CheckConsul.check(consul, maxAttempts, delayMillisBetweenAttempts);
    }
}
