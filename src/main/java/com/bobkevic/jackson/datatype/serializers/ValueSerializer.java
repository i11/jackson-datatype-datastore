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

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.cloud.datastore.Value;

class ValueSerializer extends ReferenceTypeSerializer<Value<?>> {

  ValueSerializer(final ReferenceType fullType,
                  final boolean staticTyping,
                  final TypeSerializer typeSerializer,
                  final JsonSerializer<Object> valueSerializer) {
    super(fullType, staticTyping, typeSerializer, valueSerializer);
  }

  private ValueSerializer(
      final ReferenceTypeSerializer<?> base,
      final BeanProperty property,
      final TypeSerializer typeSerializer,
      final JsonSerializer<?> valueSerializer,
      final NameTransformer nameTransformer,
      final JsonInclude.Include contentIncl) {
    super(base, property, typeSerializer, valueSerializer, nameTransformer, contentIncl);
  }

  @Override
  protected ReferenceTypeSerializer<Value<?>> withResolved(final BeanProperty property,
                                                           final TypeSerializer typeSerializer,
                                                           final JsonSerializer<?> valueSerializer,
                                                           final NameTransformer nameTransformer,
                                                           final JsonInclude.Include contentIncl) {
    return new ValueSerializer(this,
        property,
        typeSerializer,
        valueSerializer,
        nameTransformer,
        contentIncl);
  }

  @Override
  protected boolean _isValueEmpty(Value<?> value) {
    return isNull(value.get());
  }

  @Override
  protected Object _getReferenced(Value<?> value) {
    return value.get();
  }

  @Override
  protected Object _getReferencedIfPresent(Value<?> value) {
    return value.get();
  }

}
