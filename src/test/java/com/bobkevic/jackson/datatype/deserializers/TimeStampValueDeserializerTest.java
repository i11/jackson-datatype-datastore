package com.bobkevic.jackson.datatype.deserializers;

/*-
 * #%L
 * jackson-datatype-datastore
 * %%
 * Copyright (C) 2017 Ilja Bobkevic
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.bobkevic.jackson.datatype.Caster.cast;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.bobkevic.jackson.datatype.DatastoreModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class TimeStampValueDeserializerTest {

  private ObjectMapper json;

  @Before
  public void setUp() {
    json = new ObjectMapper().registerModule(new DatastoreModule())
        .registerModule(new JavaTimeModule());
  }

  @Test
  public void testFullEntityDeserialization() throws IOException {
    final String fixutre =
        "{\"test-key-1\": \"2000-01-01T12:00Z\", \"test-key-2\": \"2000-01-01T12:01Z\"}";
    final FullEntity fullEntity = json.readValue(fixutre, FullEntity.class);

    assertThat(fullEntity.getTimestamp("test-key-1").toSqlTimestamp().toInstant(),
        equalTo(ZonedDateTime.parse("2000-01-01T12:00Z").toInstant()));
    assertThat(fullEntity.getTimestamp("test-key-2").toSqlTimestamp().toInstant(),
        equalTo(ZonedDateTime.parse("2000-01-01T12:01Z").toInstant()));
  }

  @Test
  public void testMapDeserialization() throws IOException {
    final String fixutre =
        "{\"test-key-1\": \"2000-01-01T12:00Z\", \"test-key-2\": \"2000-01-01T12:01Z\"}";
    final MapType mapType =
        json.getTypeFactory().constructMapType(Map.class, String.class, Value.class);
    final Map<String, Value<?>> valueMap = json.readValue(fixutre, mapType);

    final TimestampValue dateTimeValue1 = cast(valueMap.get("test-key-1"));
    final TimestampValue dateTimeValue2 = cast(valueMap.get("test-key-2"));

    assertThat(dateTimeValue1.get().toSqlTimestamp().toInstant(),
        equalTo(ZonedDateTime.parse("2000-01-01T12:00Z").toInstant()));
    assertThat(dateTimeValue2.get().toSqlTimestamp().toInstant(),
        equalTo(ZonedDateTime.parse("2000-01-01T12:01Z").toInstant()));
  }

//  @Test
//  public void testListTypeRefernceDeserialization() throws IOException {
//    final String fixutre = "[\"2000-01-01T12:00Z\", \"2000-01-01T12:01Z\"]";
//    final TypeReference<List<Value<?>>> typeReference = new TypeReference<List<Value<?>>>() {
//    };
//    final List<Value<?>> list = json.readValue(fixutre, typeReference);
//
//    final DateTimeValue dateTimeValue1 = (DateTimeValue) valueMap.get("test-key-1");
//    final DateTimeValue dateTimeValue2 = (DateTimeValue) valueMap.get("test-key-2");
//
//    assertThat(dateTimeValue1.get().toDate().toInstant(),
//        equalTo(ZonedDateTime.parse("2000-01-01T12:00Z").toInstant()));
//    assertThat(dateTimeValue2.get().toDate().toInstant(),
//        equalTo(ZonedDateTime.parse("2000-01-01T12:01Z").toInstant()));
//    assertThat(list.contains(LongValue.of(1)), is(true));
//    assertThat(list.contains(LongValue.of(2)), is(true));
//  }

//  @Test
//  public void testListValueDeserialization() throws IOException {
//    final String fixutre = "[1, 2]";
//    final ListValue listValue = json.readValue(fixutre, ListValue.class);
//    List<? extends Value<?>> list = listValue.get();
//    assertThat(list.contains(LongValue.of(1)), is(true));
//    assertThat(list.contains(LongValue.of(2)), is(true));
//  }
}
