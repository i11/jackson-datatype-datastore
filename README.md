## Overview

This is a [Jackson](../../../jackson) modules needed to support Google Datastore value entities.

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).


## Status

[![Build Status](https://travis-ci.org/i11/jackson-datatype-datastore.svg)](https://travis-ci.org/i11/jackson-datatype-datastore)

## Usage

### Maven dependencies

```xml
<dependency>
    <groupId>com.bobkevic.jackson.datatype</groupId>
    <artifactId>jackson-module-datastore</artifactId>
</dependency>
```

### Registering modules

```java
final ObjectMapper mapper = new ObjectMapper()
   .registerModule(new DatastoreModule());
```

or, alternatively, you can also auto-discover these modules with:

```java
final ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

Either way, after registration all functionality is available for all normal Jackson operations.
