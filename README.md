# Anti-fraud Service with Spring Security
Work in progress. I keep some commented out code until I get close to finishing the project. A practice that I avoid when working with others. You can review the project with GitHub's IDE https://github.dev/nikosath/anti-fraud-app.
## Description
A Spring Security RESTful web service built to explore user authentication, authorization, and fraud detection fundamentals. Demonstrating anti-fraud principles in finance, it employs an expanded role model and user interaction endpoints, with internal transaction validation based on heuristic rules. Specs provided by the Hyperskill educational platform.

### Transaction Validation API

A set of operations related to the core business domain: validating monetary transactions. Backed by `TransactionValidationController`, a REST controller responsible for handling transaction validation and feedback within an anti-fraud system. It provides the following operations:

1. **Transaction Validation (`POST`)**: Validates a transaction based on the provided details such as amount, IP address, credit card number, geographical region, and transaction date. It returns a response indicating whether the transaction is approved or not, along with any relevant information.

2. **Validation Feedback (`PUT`)**: Allows for the provision of feedback on a previously validated transaction. This feedback can be used to override the initial automated verdict. The method takes in the transaction ID and the feedback status, returning a success response if the feedback was processed successfully or an error response otherwise.

3. **Get All Transactions (`GET`)**: Retrieves a list of all transactions along with their validation feedback. This operation is useful for obtaining a historical view of all transactions processed by the system.

4. **Get Transactions by Card Number (`GET`)**: Filters and retrieves transactions based on a specific credit card number. This allows for a focused view of transactions associated with a particular card.

### User Account Management API

A set of CRUD operations for the user accounts authorized to access the current service. Backed by `AuthController`, a REST controller providing the following operations:

1. **Create User (`POST`)**: Allows for the creation of a new user account. It accepts user details such as name, username, and password, and returns a response indicating the result of the operation, including the user profile if the creation is successful.

2. **List Users (`GET`)**: Retrieves a list of all user profiles in the system. This operation is useful for administrative purposes, allowing for an overview of all users.

3. **Delete User (`DELETE`)**: Enables the deletion of a user account based on the username. It returns a response indicating the result of the deletion operation, including a success message if applicable.

4. **Update User Role (`PUT`)**: Allows for updating the role of an existing user. This operation is crucial for managing user permissions and access levels within the application. It accepts a username and the new role to be assigned.

5. **Update User Lock Status (`PUT`)**: Manages the lock status of a user account, enabling or disabling user access. It accepts a username and the desired lock operation (lock or unlock), returning a response that reflects the new lock status.

# Notes about design and implementation

## Experimenting with:
  - Testing with reusable custom fake objects (e.g. [FakeAuthService](src/test/java/antifraud/security/service/FakeAuthService.java)) instead of framework mocks e.g. Mockito mocks.
- Using a custom [Result](src/main/java/antifraud/error/Result.java) type, inspired by [Vavr's Either type](https://docs.vavr.io/#_either). I throw custom Runtime exceptions only in exceptional cases, indicating there's a bug that needs to be fixed.
- Java 21 features: records, enhanced switch, and later on, pattern matching.
## Naming, organizing, and other choices:
I have no strong feelings about the following.
- I am using the C# interface naming convention for both interfaces and abstract classes, e.g. IAuthService.
- Packages are organized first by functionality and second by technical layer.
- Test methods are named using the pattern methodName_givenCondition_thenResult or methodName_expectResult.
- Sometimes I prefer to use anonymous classes over lambdas for clarity.

