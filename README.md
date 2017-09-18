## Overview

This is a [Jackson](../../../jackson) modules needed to support Google Datastore value entities.

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).


## Status

[![Build Status](https://travis-ci.org/i11/jackson-datatype-datastore.svg)](https://travis-ci.org/i11/jackson-datatype-datastore)
[![Coverage Status](https://coveralls.io/repos/github/i11/jackson-datatype-datastore/badge.svg?branch=master)](https://coveralls.io/github/i11/jackson-datatype-datastore?branch=master)

## Usage

### Maven dependencies
```xml
<dependency>
    <groupId>com.bobkevic.jackson.datatype</groupId>
    <artifactId>jackson-module-datastore</artifactId>
</dependency>
```
**_Note that you also need to depend on the jsr310 module_**

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```


### Registering modules
**_Note that the JavaTimeModule is also added_**
```java
final ObjectMapper mapper = new ObjectMapper()
   .registerModule(new DatastoreModule())
   .registerModule(new JavaTimeModule());
```

or, alternatively, you can also auto-discover these modules with:

```java
final ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

Either way, after registration all functionality is available for all normal Jackson operations.
