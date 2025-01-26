package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import java.util.List;

/**
 * Checks behavior of the {@link org.kiwiproject.consul.util.failover.strategy.BlacklistingConsulFailoverStrategy}
 * using a single thread.
 */
@SuppressWarnings("all")
public class CheckConsulBlacklisting {
    public static void main(String[] args) {
        var targets = List.of(
                HostAndPort.fromString("a.foo.bar.baz:8500"),
                HostAndPort.fromString("b.foo.bar.baz:8500"),
                HostAndPort.fromString("c.foo.bar.baz:8500")
        );

        var delayMillis = 25L;

        var consul = Consul.builder()
                .withPing(false)
                .withMultipleHostAndPort(targets, delayMillis)  // uses BlacklistingConsulFailoverStrategy
                .build();

        var maxAttempts = 10;
        var delayMillisBetweenAttempts = 500;
        CheckConsul.check(consul, maxAttempts, delayMillisBetweenAttempts);
    }
}
