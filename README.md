# Pact 简介

[https://github.com/DiUS/pact-jvm](https://github.com/DiUS/pact-jvm)

---

## Consumer Driven Contract

- [消费者驱动的契约](http://www.infoq.com/cn/articles/consumer-driven-contracts)
- [Consumer-Driven Contracts: A Service Evolution Pattern](https://martinfowler.com/articles/consumerDrivenContracts.html)

---

## 环境准备：

- JDK 8
- Maven 3.3.3
- IntelliJ IDEA（推荐）或 Eclipse

---

## Pact-jvm 版本

au.com.dius:pact-jvm-\***_2.11**:**2.4.18**

---

## 目录结构

```unknown
├── pact-example-consumer  
│   ├── pom.xml  # Consumer 配置
│   ├── src  
│   │   ├── main  
│   │   │   └── java  
│   │   └── test  
│   │       └── java  # Consumer 测试代码
│   └── target  
│       └── pacts  # 生成的 Pact 文件
├── pact-example-parent  
│   └── pom.xml  
└── pact-example-provider  
    └── pom.xml  # Provider 配置
```

---

## Consumer Example

![pact-consumer](https://dius.imgix.net/2016/02/Pact1.png)

---

### Consumer POM

```xml
<dependencies>
    <dependency>
        <groupId>au.com.dius</groupId>
        <artifactId>pact-jvm-consumer-junit_2.11</artifactId>
        <version>2.4.18</version>
        <scope>test</scope>
    </dependency>

    <!--可选，方便验证-->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>3.0.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Consumer Pact Example

[PactExampleTest](pact-example-consumer/src/test/java/PactExampleTest.java)

还可以写成其他风格：

- junit: [Defect215Test](pact-example-consumer/src/test/java/Defect215Test.java)
- spring-test: [InstancesFacadePactTest](https://github.com/whunmr/spring-server/blob/master/rds/src/test/java/com/dddsample/rds/interfaces/InstancesFacadePactTest.java)

---

### Consumer 组件

- provider
- consumer
- PactFragment
- test

---

### PACT DSL

```java
private static final String APPLICATION_JSON = "application/json.*";

return builder
        .uponReceiving("Check latest dollar and pound")
        .method("GET")
        .path("/latest")
        .matchHeader(CONTENT_TYPE, APPLICATION_JSON) // 正则表达式匹配
        .query("symbols=USD,GBP").
            willRespondWith()
            .status(200)
            .body(jsonBody)
            .toFragment();
```

---

### JSON DSL

```java
DslPart jsonBody = new PactDslJsonBody()
        .stringValue("base", "EUR")
        .date("date")
        .object("rates")
        .decimalType("GBP")
        .decimalType("USD")
        .closeObject();
```

---

### Consumer Test

```java
given()
        .baseUri(url)
        .queryParams("symbols", "USD,GBP")
        .contentType(APPLICATION_JSON_).
        when()
        .get("latest").
        then()
        .statusCode(200)
        .body("base", equalTo("EUR"));
```

> 这里用了[Rest Assured](https://github.com/rest-assured/rest-assured/wiki/Usage)

---

### Run consumer

```sh
mvn test
```

---

### Pact File

[Pact File Example](pact-example-consumer/target/pacts/org.qinyu-currency.json)

---

## Provider Example

![pact-provider](https://dius.imgix.net/2016/02/Pact2.png)

---

### Provider POM

```xml
<build>
    <plugins>
        <plugin>
            <groupId>au.com.dius</groupId>
            <artifactId>pact-jvm-provider-maven_2.11</artifactId>
            <version>2.4.18</version>
            <configuration>
                <serviceProviders>
                    <!-- 可以定义多个 Provider，但名字唯一 -->
                    <serviceProvider>
                        <name>org.qinyu</name>
                        <insecure>true</insecure>
                        <!-- 属性都是可选的 -->
                        <protocol>https</protocol>  <!-- 默认 http -->
                        <host>api.fixer.io</host>  <!-- 默认 localhost -->
                        <port>443</port>  <!-- 默认 8080 -->
                        <path>/</path>   <!-- 默认 / -->
                        <consumers>
                            <!-- 可以定义多个 Provider，但名字唯一 -->
                            <consumer>
                                <name>currency</name>
                                <!-- 路径或 broker url -->
                                <pactFile>../pact-example-consumer/target/pacts/org.qinyu-currency.json</pactFile>
                            </consumer>
                        </consumers>
                    </serviceProvider>
                </serviceProviders>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

### Provider 验证

```sh
mvn pact:verify
```