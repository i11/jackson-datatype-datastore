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
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ValueDeserializer extends ReferenceTypeDeserializer<Value<?>> {

  ValueDeserializer(final JavaType fullType,
                    final TypeDeserializer typeDeser,
                    final JsonDeserializer<?> deser) {
    super(fullType, typeDeser, deser);
  }

  /**
   * Try to parse given string as date.
   *
   * @param dateTime supposedly date time string
   * @return Optional date time object or empty optional if parsing failed
   */
  static Optional<ZonedDateTime> parseDate(final String dateTime) {
    try {
      return Optional.of(ZonedDateTime.parse(dateTime));
    } catch (final DateTimeParseException e) {
      // ignore
    }
    return Optional.empty();
  }

  static Value timeStampValueOf(final ZonedDateTime dateTime) {
    return TimestampValue.of(Timestamp.of(Date.from(dateTime.toInstant())));
  }

  @Override
  protected ReferenceTypeDeserializer<Value<?>> withResolved(final TypeDeserializer typeDeser,
                                                             final JsonDeserializer<?> valueDeser) {
    return new ValueDeserializer(_fullType, typeDeser, valueDeser);
  }

  @Override
  public Value<?> getNullValue(final DeserializationContext ctxt) {
    return NullValue.of();
  }

  // TODO: Protect against stack overflow
  @Override
  public Value<?> referenceValue(final Object contents) {
    if (isNull(contents)) {
      return NullValue.of();
    }

    final Class<?> clazz = contents.getClass();
    if (FullEntity.class.isAssignableFrom(clazz)) {
      return EntityValue.of(cast(contents));
    } else if (Boolean.class.isAssignableFrom(clazz)) {
      return BooleanValue.of(cast(contents));
    } else if (Double.class.isAssignableFrom(clazz)) {
      return DoubleValue.of(cast(contents));
    } else if (Long.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
      return LongValue.of(Long.valueOf(contents.toString()));
    } else if (String.class.isAssignableFrom(clazz)) {
      final String value = contents.toString();
      final Optional<ZonedDateTime> zonedDateTime = parseDate(value);
      return zonedDateTime
          .map(ValueDeserializer::timeStampValueOf)
          .orElseGet(() ->
              StringValue.newBuilder(value)
                  .setExcludeFromIndexes(value.getBytes().length > 1500)
                  .build());
    } else if (ZonedDateTime.class.isAssignableFrom(clazz)) {
      return TimestampValue.of(Timestamp.of((Date.from(cast(contents)))));
    } else if (Instant.class.isAssignableFrom(clazz)) {
      return TimestampValue.of(Timestamp.of((Date.from(cast(contents)))));
    } else if (List.class.isAssignableFrom(clazz)) {
      final List<Object> rawList = cast(contents);
      return ListValue.of(rawList.stream()
          .map(this::referenceValue)
          .collect(toList()));
    } else if (Map.class.isAssignableFrom(clazz)) {
      final Map<String, Object> rawMap = cast(contents);
      final FullEntity.Builder<IncompleteKey> builder = FullEntity.newBuilder();
      rawMap.forEach((key, value) -> builder.set(key, referenceValue(value)));
      return EntityValue.of(builder.build());
    }

    return cast(contents);
  }
}
