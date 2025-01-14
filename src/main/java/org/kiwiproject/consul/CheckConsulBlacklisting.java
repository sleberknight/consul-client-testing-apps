package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

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
                .withMultipleHostAndPort(targets, delayMillis)
                .build();

        var maxAttempts = 10;
        var delayMillisBetweenAttempts = 500;
        CheckConsul.check(consul, maxAttempts, delayMillisBetweenAttempts);
    }
}
