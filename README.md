This repository was created for functional testing of [consul-client](https://github.com/kiwiproject/consul-client),
specifically to test the behavior of different `ConsulFailoverStrategy` implementations.

It is basically just some small "main" apps that you can run.

Note that it depends on consul-client 1.5.0-SNAPSHOT as of this writing, so you will first need to install it.
And make sure to switch to a PR branch before installing if you are testing a new implementation.

Alternatively, copy and paste the Java files directly into `consul-client` in the same package (
`org.kiwiproject.consul`), and copy the `logback.xml` into `src/main/resources`. Again, to test a new failover strategy
not in the main branch, make sure to switch to the appropriate branch first. This makes it easy to iterate by changing
the strategy directly, re-running the apps, etc.
