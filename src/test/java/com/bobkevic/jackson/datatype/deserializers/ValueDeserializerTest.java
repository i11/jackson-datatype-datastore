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

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.bobkevic.jackson.datatype.DatastoreModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

public class ValueDeserializerTest {

  private ObjectMapper json;

  @Before
  public void setUp() {
    json = new ObjectMapper().registerModule(new DatastoreModule());
  }

  @Test
  public void testDeserializationOfEntityValue() throws IOException {
    final String entityFixture =
        "{\"blob-check-key\":\"AQID\",\"test-array\":[1000,\"string-element1\",true,{\"embeded-key\":\"because-i-can\"}],\"test-key\":\"test-value\", \"key-to-null\": null}";
    final EntityValue entityValue = json.readValue(entityFixture, EntityValue.class);
    final FullEntity<?> fullEntity = entityValue.get();
    final List<Value<?>> valueList = fullEntity.getList("test-array");
    assertThat(fullEntity.getString("blob-check-key"), is("AQID"));
    assertThat(fullEntity.getString("test-key"), is("test-value"));
    assertThat(fullEntity.getString("key-to-null"), is((String) null));
    assertThat(valueList.contains(LongValue.of(1000L)), is(true));
    assertThat(valueList.contains(StringValue.of("string-element1")), is(true));
    assertThat(valueList.contains(BooleanValue.of(true)), is(true));
  }

  @Test
  public void testDeserializationOfFullEntity() throws IOException {
    final String entityFixture =
        "{\"blob-check-key\":\"AQID\",\"test-array\":[1000,\"string-element1\",true,{\"embeded-key\":\"because-i-can\"}],\"test-key\":\"test-value\", \"key-to-null\": null}";
    final FullEntity fullEntity = json.readValue(entityFixture, FullEntity.class);
    final List<Value<?>> valueList = fullEntity.getList("test-array");
    assertThat(fullEntity.getString("blob-check-key"), is("AQID"));
    assertThat(fullEntity.getString("test-key"), is("test-value"));
    assertThat(fullEntity.getString("key-to-null"), is((String) null));
    assertThat(valueList.contains(LongValue.of(1000L)), is(true));
    assertThat(valueList.contains(StringValue.of("string-element1")), is(true));
    assertThat(valueList.contains(BooleanValue.of(true)), is(true));
  }

  @Test
  public void testDeserializeRealFixture() throws IOException {
    final String fixture =
        Resources.toString(getResource(this.getClass(), "pr.json"), defaultCharset());
    final EntityValue entityValue = json.readValue(fixture, EntityValue.class);
    final FullEntity<?> entity = entityValue.get();
    MatcherAssert.assertThat(entity.getString("action"), is("opened"));
    MatcherAssert.assertThat(entity.getLong("number"), is(1L));
    MatcherAssert.assertThat(entity.getEntity("pull_request"), notNullValue());
    MatcherAssert.assertThat(entity.getEntity("pull_request").getEntity("user"), notNullValue());
    MatcherAssert.assertThat(entity.getEntity("pull_request").getEntity("user").getString("login"),
        is("baxterthehacker"));

  }
}
