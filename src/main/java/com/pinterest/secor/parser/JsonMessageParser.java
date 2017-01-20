/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.secor.parser;

import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.message.Message;
import com.pinterest.secor.parser.exceptions.DateFieldParsingException;
import jdk.nashorn.internal.runtime.ParserException;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JsonMessageParser extracts timestamp field (specified by 'message.timestamp.name')
 * from JSON data and partitions data by date.
 */
public class JsonMessageParser extends TimestampedMessageParser {
    private final boolean m_timestampRequired;
    private String inputPattern;


    public JsonMessageParser(SecorConfig config) {
        super(config);
        m_timestampRequired = config.isMessageTimestampRequired();
        inputPattern = mConfig.getMessageTimestampInputPattern();
    }

    @Override
    public long extractTimestampMillis(final Message message) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(message.getPayload());
        if (jsonObject != null) {
            Object fieldValue = getJsonFieldValue(jsonObject);
            if (fieldValue != null) {
                if(inputPattern != null){
                    try{
                        return DateUtils.parseDate(fieldValue.toString(), new String[]{inputPattern}).getTime();
                    } catch (ParseException e) {
                        throw new DateFieldParsingException("Unable to parse Date field", e);
                    }
                }else{
                    return toMillis(Double.valueOf(fieldValue.toString()).longValue());
                }

            }
        } else if (m_timestampRequired) {
            throw new RuntimeException("Missing timestamp field for message: " + message);
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        Date parsed = DateUtils.parseDate("2016-01-01T10:00:01.123456", new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSSSS"});
        System.out.println(parsed.getTime());

    }
}
