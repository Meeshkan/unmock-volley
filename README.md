# [Unmock](https://www.unmock.io/) (Volley SDK)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.unmock/volley/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.unmock/volley)
[![CircleCI](https://circleci.com/gh/unmock/unmock-volley.svg?style=svg)](https://circleci.com/gh/unmock/unmock-volley)
[![Known Vulnerabilities](https://snyk.io/test/github/unmock/unmock-volley/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/unmock/unmock-volley?targetFile=build.gradle)
[![Chat on Gitter](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/unmock/community)

Public API mocking for Java, Scala, Kotlin, Clojure, Groovy and friends.

Unmock can be used to test modules that perform requests to third-party APIs like Hubspot, SendGrid, Behance, and hundreds of other public APIs.

`unmock-volley` is a high-level interceptor for Android apps using [Volley](https://developer.android.com/training/volley).

The ultimate goal of unmock is to provide a semantically and functionally adequate mock of the internet.

## How does it work?

The `unmock-volley` package uses a custom request queue to replace the Volley request queue.  In real applications, this should be injected via a mechanism like [Dagger](https://github.com/google/dagger).

```java
  @Test
  public void testCallingBehanceAPI() throws IOException {
    final RequestQueue queue = UnmockRequestQueue.newRequestQueueForTest(RuntimeEnvironment.application, new UnmockOptions.Builder().build());
    String url = "https://www.behance.net/v2/projects";
    final BodyHolder body = new BodyHolder();

    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    body.body = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
    );

    queue.add(stringRequest);
    Thread.sleep(5000); // hack
    JSONObject json = new JSONObject(body.body);
    JSONArray projects = json.getJSONArray("projects");
    Assert.assertTrue(projects.getJSONObject(0).getInt("id") >= 0);
  }
```

## Install

```gradle
dependencies {
  compile group: 'io.unmock', name: 'volley', version: '0.0.0';
  compile group: 'io.unmock', name: 'core', version: '0.0.0';
}
```

### Java version support

Unmock is written in Java 1.8.  It is currently untested for older versions of Java but may work for them as well.

## unmock.io

The URLs printed to the command line are hosted by [unmock.io](https://www.unmock.io).  You can consult the documentation about that service [here](https://www.unmock.io/docs).

## Contributing

Thanks for wanting to contribute! Take a look at our [Contributing Guide](CONTRIBUTING.md) for notes on our commit message conventions and how to run tests.

Please note that this project is governed by the [Meeshkan Community Code of Conduct](https://github.com/meeshkan/code-of-conduct). By participating in this project, you agree to abide by its terms.

## License

[MIT](LICENSE)

Copyright (c) 2018‚ 2019 [Meeshkan](http://meeshkan.com) and other [contributors](https://github.com/meeshkan/unmock-js/graphs/contributors).
