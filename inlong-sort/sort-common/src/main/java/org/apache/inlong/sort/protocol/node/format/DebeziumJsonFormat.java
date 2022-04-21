/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.sort.protocol.node.format;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * parameter reference
 * https://nightlies.apache.org/flink/flink-docs-release-1.13/zh/docs/connectors/table/formats/debezium
 */
@JsonTypeName("debeziumJsonFormat")
@Data
public class DebeziumJsonFormat implements Format {

    @JsonProperty(value = "schemaInclude", defaultValue = "false")
    private Boolean schemaInclude;
    @JsonProperty(value = "ignoreParseErrors", defaultValue = "true")
    private Boolean ignoreParseErrors;
    @JsonProperty(value = "timestampFormatStandard", defaultValue = "SQL")
    private String timestampFormatStandard;
    @JsonProperty(value = "mapNullKeyMode", defaultValue = "DROP")
    private String mapNullKeyMode;
    @JsonProperty(value = "mapNullKeyLiteral", defaultValue = "null")
    private String mapNullKeyLiteral;
    @JsonProperty(value = "encodeDecimalAsPlainNumber", defaultValue = "true")
    private Boolean encodeDecimalAsPlainNumber;

    @JsonCreator
    public DebeziumJsonFormat(@JsonProperty(value = "schemaInclude", defaultValue = "false") Boolean schemaInclude,
            @JsonProperty(value = "ignoreParseErrors", defaultValue = "true") Boolean ignoreParseErrors,
            @JsonProperty(value = "timestampFormatStandard", defaultValue = "SQL") String timestampFormatStandard,
            @JsonProperty(value = "mapNullKeyMode", defaultValue = "DROP") String mapNullKeyMode,
            @JsonProperty(value = "mapNullKeyLiteral", defaultValue = "null") String mapNullKeyLiteral,
            @JsonProperty(value = "encodeDecimalAsPlainNumber", defaultValue = "true")
                    Boolean encodeDecimalAsPlainNumber) {
        this.schemaInclude = schemaInclude;
        this.ignoreParseErrors = ignoreParseErrors;
        this.timestampFormatStandard = timestampFormatStandard;
        this.mapNullKeyMode = mapNullKeyMode;
        this.mapNullKeyLiteral = mapNullKeyLiteral;
        this.encodeDecimalAsPlainNumber = encodeDecimalAsPlainNumber;
    }

    @JsonCreator
    public DebeziumJsonFormat() {
        this(false, true, "SQL", "DROP",
                "null", true);
    }

    @JsonIgnore
    @Override
    public String getFormat() {
        return "debezium-json";
    }
}
