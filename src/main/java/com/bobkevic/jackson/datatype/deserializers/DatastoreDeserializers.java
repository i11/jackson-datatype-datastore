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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Value;

public final class DatastoreDeserializers extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findReferenceDeserializer(final ReferenceType refType,
                                                       final DeserializationConfig config,
                                                       final BeanDescription beanDesc,
                                                       final TypeDeserializer contentTypeDeserializer,
                                                       final JsonDeserializer<?> contentDeserializer) {
    final Class<?> rawClass = refType.getRawClass();
    if (Value.class.isAssignableFrom(rawClass)) {
      return new ValueDeserializer(refType, contentTypeDeserializer, contentDeserializer);
    }

    return null;
  }

  @Override
  public JsonDeserializer<?> findBeanDeserializer(final JavaType type,
                                                  final DeserializationConfig config,
                                                  final BeanDescription beanDesc)
      throws JsonMappingException {
    final Class<?> rawClass = type.getRawClass();

    if (FullEntity.class.isAssignableFrom(rawClass)) {
      return FullEntityDeserializer.INSTANCE;
    }
    return super.findBeanDeserializer(type, config, beanDesc);
  }
}
