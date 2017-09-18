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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.cloud.Timestamp;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

class TimeStampSerializer extends JsonSerializer<Timestamp> {

  static final TimeStampSerializer INSTANCE = new TimeStampSerializer();

  @Override
  public void serialize(final Timestamp value,
                        final JsonGenerator gen,
                        final SerializerProvider provider)
      throws IOException {
    if (Objects.nonNull(value)) {
      provider
          .findValueSerializer(Instant.class)
          .serialize(value.toSqlTimestamp().toInstant(), gen, provider);
    } else {
      gen.writeNull();
    }
  }
}
