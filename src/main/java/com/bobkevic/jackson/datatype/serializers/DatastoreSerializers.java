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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.DateTime;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Value;

public final class DatastoreSerializers extends Serializers.Base {

  @Override
  public JsonSerializer<?> findReferenceSerializer(final SerializationConfig config,
                                                   final ReferenceType refType,
                                                   final BeanDescription beanDesc,
                                                   final TypeSerializer typeSerializer,
                                                   final JsonSerializer<Object> valueSerializer) {
    final Class<?> rawClass = refType.getRawClass();
    if (Value.class.isAssignableFrom(rawClass)) {
      boolean staticTyping = (typeSerializer == null)
                             && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
      return new ValueSerializer(refType, staticTyping, typeSerializer, valueSerializer);
    }
    return null;
  }

  @Override
  public JsonSerializer<?> findSerializer(final SerializationConfig config,
                                          final JavaType type,
                                          final BeanDescription beanDesc) {
    final Class<?> rawClass = type.getRawClass();
    if (FullEntity.class.isAssignableFrom(rawClass)) {
      return FullEntitySerializer.INSTANCE;
    } else if (DateTime.class.isAssignableFrom(rawClass)) {
      return DateTimeSerializer.INSTANCE;
    } else if (Blob.class.isAssignableFrom(rawClass)) {
      return BlobSerializer.INSTANCE;
    }
    return super.findSerializer(config, type, beanDesc);
  }
}
