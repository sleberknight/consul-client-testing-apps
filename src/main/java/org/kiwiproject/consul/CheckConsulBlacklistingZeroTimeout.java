package org.kiwiproject.consul;

import com.google.common.net.HostAndPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class CheckConsulBlacklistingZeroTimeout {

    public static void main(String[] args) {
        var targets = List.of(
            HostAndPort.fromString("a.foo.bar.baz:8500"),
            HostAndPort.fromString("b.foo.bar.baz:8500"),
            HostAndPort.fromString("c.foo.bar.baz:8500")
        );

        var delayMillis = 0L;
        var consul = Consul.builder()
                .withPing(false)
                .withMultipleHostAndPort(targets, delayMillis)
                .build();

        throw new IllegalStateException("should not get here!");
    }
}