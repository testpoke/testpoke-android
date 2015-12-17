Changes in version 2.0.0 (2015-04-13)
--------------------------------------

- [Issue #1] - Removed support for auto logcat crawler on session close
- [Issue #3] - Defer support for memory monitor for widget knowledge version
- [Issue #4] - Session components are working/persisting data after session is reported not-active
- [Issue #2] - Sessions aborted due on bad user-agent on trust
- [Issue #5] - "ID" field is expected on install object in first trust request
- [Issue #6] - trust process must send User-Entity request header
- [Issue #7] - Active session is going to be removed on sessions dispatch
- [Issue #8] - Race condition on sessions components sometimes produce NullPointerException
- [Issue #9] - IdleWatcherMonitor is going to be fired on closed sessions
- [Issue #10] - TestPoke skip 0 bytes generating apk token



Changes in version 2.0.1 (2015-05-08)
--------------------------------------

- [Issue #12] - Thread starting during runtime shutdown were crash happens
- [Issue #11] - Connection & socket timeout to high
- [Issue #13] - User signed up to TestPoke is not sent correctly


Changes in version 2.0.2 (2015-06-30)
--------------------------------------

- [Issue #14] - Unclosed sessions has been sent
- [Issue #15] - Send "serial" field under "install" trust object


Changes in version 2.0.3 (2015-07-09)
--------------------------------------

- [Issue #16] - "start" and "end" dates of sessions being reported corrupted


Changes in version 2.0.4 (2015-07-23)
--------------------------------------

- [Issue #17] - Allow orphan sessions to be sent
- [Issue #18] - Send empty IMEI in device that don't report imei
- [Issue #19] - Configure the SDK to resend device install on SDK version change
- [Issue #23] - Android 6.0 Non-Public Class Security Bug
- [Issue #21] - Avoid actual session to be sent until its closed properly
- [Issue #22] - Turn SDK to be idempotent on init


Changes in version 2.0.5 (2015-11-15)
--------------------------------------

- Performance improvements sending remote data
- Removing unnecessary pack data decoders
- Adoption `aar` distribution mechanism in order to make the library public available at central
  repositories.
- Changed `api-token` by `app-token` in xml SDK configuration



Changes in version 2.0.6 (2015-12-15)
--------------------------------------

- [Bug TPA1] - Fixed, Background opened sessions aren't been closed properly 
- [Bug TPA5] - Fixed, Android httpclient hangs on repeated requests to the server on not response 

 


