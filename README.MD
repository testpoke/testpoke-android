TestPoke Android
================

TestPoke is a library that let developers monitor the performance of their Android app during users sessions through a
friendly user interface, the library send app usage information and possible runtime crashes. it targeted to developers
that want to be notified when their apps don't work as expected.


Among other capabilities TestPoke provides you, these are a few ones:

- Crash Reporting Tracking in automatic fashion.
- Keep affine control of you data app state on crash time, you can see current configuration, filtered `SharedPreferences` in
  friendly way through your [App TestPoke Dashboard][site].
- You can send report info even if the app doesn't crash.
- Send `Logs`, `Errors`, `Exceptions`, `Events` by yourself.
- Track users behavior during their sessions.
- You can configure TestPoke to handle by yourself users sessions.
- Integrate with many apps you love.


Getting Started
----------------

For latest update in the documentation you can see [TestPoke Documentation][doc]

Integrate the latest SDK version in your app.

Using Maven


```xml

<dependency>
  <groupId>com.testpoke</groupId>
  <artifactId>testpoke-android</artifactId>
  <version>2.1.0</version>
</dependency>

```

or Gradle:
```groovy
compile 'com.testpoke:testpoke-android:2.1.0'
```


Change Log
----------

For a complete change log, please check it out [Change Log][1]

License
-------

    Copyright 2013 TestPoke, Inc.

    Please see the project README file for additional information.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this work except in compliance with the License.
    You may obtain a copy of the License below, or at:

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[site]: https://app.testpoke.com
[doc]: http://dev.testpoke.com/docs/overview


[1]: https://github.com/testpoke/testpoke-android/blob/master/CHANGELOG.MD






