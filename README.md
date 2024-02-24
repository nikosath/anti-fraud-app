# Anti-fraud app with Spring Boot
Work in progress. Less than halfway done. You can review the project with GitHub's IDE https://github.dev/nikosath/anti-fraud-app
## I am experimenting with:
  - Testing with reusable custom fake objects (e.g. [FakeAuthService](src/test/java/antifraud/security/service/FakeAuthService.java)) instead of framework mocks e.g. Mockito mocks.
- [Vavr's Either type](https://docs.vavr.io/#_either) for a more functional error handling.
- Java 21 features: records, enhanced switch, and later on, pattern matching.
## Considerations/Notes:
- I am using the C# interface naming convention for both interfaces and abstract classes, e.g. IAuthService.
- Packages are organized first by functionality and second by technical layer.
- Test methods are named using the pattern methodName_givenCondition_thenResult or methodName_expectResult.
- Sometimes I prefer to use anonymous classes over lambdas for clarity.
- I keep some commented out code until I get close to finishing the project. A practice that I avoid when working others.

