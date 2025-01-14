package org.kiwiproject.consul;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class CheckConsul {

    public static void check(Consul consul, int maxAttempts, int delayMillisBetweenAttempts) {
        check("", consul, maxAttempts, delayMillisBetweenAttempts);
    }

    public static void check(String description, Consul consul, int maxAttempts, int delayMillisBetweenAttempts) {
        var client = consul.keyValueClient();

        var prefix = isBlank(description) ? "" : String.format("[%s]: ", description);

        var lastAttemptIndex = maxAttempts - 1;
        var startNanos = System.nanoTime();

        for (int i = 0; i < maxAttempts; i++) {
            var attemptNumber = i + 1;
            try {
                System.out.printf("%sAttempt #%d to call getValueAsString%n", prefix, attemptNumber);
                var res = client.getValueAsString("foo").orElse("");
                System.out.println();
                System.out.printf("%sAttempt #%d, finished at %s with Result: %s%n", prefix, attemptNumber, Instant.now(), res);
            } catch (Exception e) {
                System.out.println();
                System.out.printf("%sAttempt #%d, finished at %s with Error: %s%n", prefix, attemptNumber, Instant.now(), e);
            }

            System.out.println();

            // sleep a bit if not on last attempt...
            if (i < lastAttemptIndex) {
                System.out.printf("%sSleeping after attempt #%d%n", prefix, attemptNumber);
                try {
                    Thread.sleep(delayMillisBetweenAttempts);
                } catch (InterruptedException ignored) {
                    System.out.printf("%sWARN: Interrupted while sleeping!%n", prefix);
                }
            }
        }

        var elapsedNanos = System.nanoTime() - startNanos;
        System.out.printf("%sTotal elapsed time: %d%n", prefix, TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
    }
}
