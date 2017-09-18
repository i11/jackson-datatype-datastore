package com.bobkevic.jackson.datatype.serializers;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ValueSerializerTest {

  private ObjectMapper json;

  @Before
  public void setUp() {
    json = new ObjectMapper()
        .registerModule(new DatastoreModule())
        .registerModule(new JavaTimeModule());
  }

  @Test
  public void testEntityValueWithListSerialization() {
    final String testKey = "test-key";
    final String testValue = "test-value";
    final String testArrayKey = "test-array";
    final String testBlobKey = "blob-check-key";
    final List<? extends Value<?>> valueList = ImmutableList.<Value<?>>builder()
        .add(TimestampValue.of(Timestamp.of((Date.from(Instant.parse("1970-01-01T00:00:01Z"))))))
        .add(StringValue.of("string-element1"))
        .add(BooleanValue.of(true))
        .add(EntityValue
            .of(FullEntity.newBuilder().set("embeded-key", "because-i-can").build()))
        .build();
    final EntityValue testEntity = EntityValue.of(
        FullEntity.newBuilder()
            .set(testKey, testValue)
            .set(testArrayKey, valueList)
            .set(testBlobKey, BlobValue.of(Blob.copyFrom(new byte[]{0x01, 0x02, 0x03})))
            .build());

    final String testValueString = uncheckedWriteValueAsString(testEntity);

    assertThat(testValueString,
        is("{\"" + testBlobKey + "\":\"AQID\",\"" + testArrayKey
           + "\":[1.000000000,\"string-element1\",true,{\"embeded-key\":\"because-i-can\"}],\""
           + testKey + "\":\"" + testValue + "\"}"));
  }

  private String uncheckedWriteValueAsString(final Object value) {
    try {
      return json.writeValueAsString(value);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed writing value: " + value, e);
    }
  }
}
