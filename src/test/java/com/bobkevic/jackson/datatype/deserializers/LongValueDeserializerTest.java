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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.bobkevic.jackson.datatype.DatastoreModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.Value;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class LongValueDeserializerTest {

  private ObjectMapper json;

  @Before
  public void setUp() {
    json = new ObjectMapper().registerModule(new DatastoreModule());
  }

  @Test
  public void testFullEntityDeserialization() throws IOException {
    final String fixutre = "{\"test-key-1\": 1, \"test-key-2\": 2}";
    final FullEntity fullEntity = json.readValue(fixutre, FullEntity.class);
    assertThat(fullEntity.getValue("test-key-1").get(), is(1L));
    assertThat(fullEntity.getValue("test-key-2").get(), is(2L));
  }

  @Test
  public void testMapDeserialization() throws IOException {
    final String fixutre = "{\"test-key-1\": 1, \"test-key-2\": 2}";
    final MapType mapType =
        json.getTypeFactory().constructMapType(Map.class, String.class, Value.class);
    final Map<String, Value<?>> valueMap = json.readValue(fixutre, mapType);
    assertThat(valueMap.get("test-key-1").get(), is(1L));
    assertThat(valueMap.get("test-key-2").get(), is(2L));
  }

  @Test
  public void testListTypeRefernceDeserialization() throws IOException {
    final String fixutre = "[1, 2]";
    final TypeReference<List<Value<?>>> typeReference = new TypeReference<List<Value<?>>>() {
    };
    final List<Value<?>> list = json.readValue(fixutre, typeReference);
    assertThat(list.contains(LongValue.of(1)), is(true));
    assertThat(list.contains(LongValue.of(2)), is(true));
  }

  @Test
  public void testListValueDeserialization() throws IOException {
    final String fixutre = "[1, 2]";
    final ListValue listValue = json.readValue(fixutre, ListValue.class);
    List<? extends Value<?>> list = listValue.get();
    assertThat(list.contains(LongValue.of(1)), is(true));
    assertThat(list.contains(LongValue.of(2)), is(true));
  }
}
