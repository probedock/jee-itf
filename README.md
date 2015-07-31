# jee-itf

> Java EE Integration Test Framework. Integration tests run inside the application container with all the transaction management available.

## Usage

1. Put the following dependency in your pom.xml

  ```xml
  <dependency>
    <groupId>io.probedock</groupId>
    <artifactId>jee-itf</artifactId>
    <version>2.1.1</version>
  </dependenc>
  ```

2. Create your EJB test controller `class` and `interface`. This controller will define the different test suites. 
You have to extend `AbstractTestController`.

  ```java
  @Local
  public interface MyTestController extends TestController {
  }
  
  @Stateless
  @TransactionManagement(TransactionManagementType.BEAN)
  public class MyTestControllerImpl extends AbstractTestController implements MyTestController {
    @EJB
    public MyFirstClassTest myFirstClassTest;
  
    // ... defines other test classes there
  }
  ```

3. Write your first test. Each of your test suites must extends `TestGroup`.

  ```java
  import io.probedock.jee.itf.TestGroup;
  
  public interface MyFirstClassTest extends TestGroup {
  }
  
  ----
  
  import io.probedock.jee.itf.annotations.Test;
  
  public class MyFirstClassTestImpl implements MyFirstClassTest {
    @EJB
    public MyEjbToTest myEjbToTest;
    
    @Test
    public Description myEjbToTestShouldDoAnAddition(Description description) {
      int result = myEjbToTest.add(2, 2);
      
      /**
       * At the moment, the framework is limited by the fact there is no builtin assertions like Junit.
       * In place, you have the full control of the description which is pass to each test or before/after methods.
       * The description MUST be returned at the end of the test.
       */
      if (result != 4) {
        return description.fail("The result is not correct");
      }
      else {
        return description.pass();
      }
    }
    
    @Override
    public TestGroup getTestGroup() {
        return this;
    }
  }
  ```
  
  **Remark**: The framework takes care the transaction management for you. Each test will be run in its own transaction that
  will be rolledbacked at the end of the test. You can create methods to be run before/after one/all test. The annotation is
  `@TestSetup` and for the tests the annotation is `@Test`. With these two annotations, you will be able to control the behavior
  of the transaction management. Running data population in/out the test transaction is possible.

4. Extends `AbstractDefaultTestResource` in your web test project.

  ```java
  import io.probedock.jee.itf.rest.AbstractDefaultTestResource;
  
  @Resource
  public class TestEndPoint extends AbstractDefaultTestResource {
    @EJB
    private MyTestController myTestController;
  
    @Override
    public TestController getController() {
      return myTestController;
    }
  }
  ```

5. Make sure the resource is exposed as standard REST resource. If you use annotations, you can follow the next example:

  ```java
  @Application
  public class TestRestApplication extends Application {
  
  }
  ```

6. Deploy your application.

7. To start the tests, you need to do `POST` request with the following content in the body. In fact, you have the full
control of the path where the resource is exposed.

  ```json
  {
    "filters": [{
      "type": "key",
      "text": "agas"
    }, {
      "type": "tag",
      "text": "feature-a"
    }],
    "seed": 123456
  }
  ```
  
  #### Main object
  
  | Name         | Mandatory | Description |
  | ------------ | --------- | ----------- |
  | filters[]    | No        | Define a list of filters to run specific tests. |
  | seed         | No        | Used to generate the test run order. If not sent, the order is random and the seed will appear in the logs. |
  
  #### Filter object
  
  | Name         | Mandatory | Description |
  | ------------ | --------- | ----------- |
  | type         | Yes       | The filter type: *, key, name, fingerprint, tag and ticket are valid values. |
  | text         | Yes       | Free text applied to filter type to match tests to run. |

### Requirements

* Java 6+

## Contributing

* [Fork](https://help.github.com/articles/fork-a-repo)
* Create a topic branch - `git checkout -b feature`
* Push to your branch - `git push origin feature`
* Create a [pull request](http://help.github.com/pull-requests/) from your branch

Please add a changelog entry with your name for new features and bug fixes.

## License

**jee-itf** is licensed under the [MIT License](http://opensource.org/licenses/MIT).
See [LICENSE.txt](LICENSE.txt) for the full text.
