# Anti-fraud Service with Spring Security
Work in progress. You can review the project with GitHub's IDE https://github.dev/nikosath/anti-fraud-app.
## Description
A Spring Security RESTful web service built to explore user authentication, authorization, and fraud detection fundamentals. Demonstrating anti-fraud principles in finance, it employs an expanded role model and user interaction endpoints, with internal transaction validation based on heuristic rules. Specs provided by the Hyperskill educational platform.

## I am experimenting with:
  - Testing with reusable custom fake objects (e.g. [FakeAuthService](src/test/java/antifraud/security/service/FakeAuthService.java)) instead of framework mocks e.g. Mockito mocks.
- Using a custom [Result](src/main/java/antifraud/error/Result.java) type, inspired by [Vavr's Either type](https://docs.vavr.io/#_either). I throw custom Runtime exceptions only in cases where I'd want to return a 5xx error. 
- Java 21 features: records, enhanced switch, and later on, pattern matching.
## Considerations/Notes:
- I am using the C# interface naming convention for both interfaces and abstract classes, e.g. IAuthService. No strong feelings about this decision.
- Packages are organized first by functionality and second by technical layer.
- Test methods are named using the pattern methodName_givenCondition_thenResult or methodName_expectResult.
- Sometimes I prefer to use anonymous classes over lambdas for clarity.
- I keep some commented out code until I get close to finishing the project. A practice that I avoid when working others.

