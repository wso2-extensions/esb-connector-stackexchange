/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.integration.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackExchangeTestUtil {
    private static final String OUTER_KEY = "items";

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static <T> List<T> getStackExchangeObjectList(StackExchangeUrl url, ResponseExtractor<T> extractor)
            throws IOException, JSONException {
        StackExchangeUrlConnection connection = new StackExchangeUrlConnection(url.openConnection());
        int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException(
                    String.format("Object extraction failed due to invalid statusCode: (code = %s)", code));
        }
        InputStream stream = connection.getInputStream();
        return extractor.extract(
                new JSONObject(IOUtils.toString(stream, "UTF-8")).getJSONArray(OUTER_KEY));
    }

    public static class FilterResponseExtractor implements ResponseExtractor<FilterIncludedFields> {
        private static final String INNER_KEY = "included_fields";

        @Override
        public List<FilterIncludedFields> extract(JSONArray itemArray) throws JSONException{
            List<FilterIncludedFields> filterIncludedFields = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);

                try {
                    JSONArray includedFields = item.getJSONArray(INNER_KEY);
                    List<String> filterKeys = new ArrayList<>();
                    for (int j = 0; j < includedFields.length(); j++) {
                        filterKeys.add(includedFields.getString(j));
                    }
                    filterIncludedFields.add(new FilterIncludedFields(filterKeys));
                } catch (JSONException e) {
                    throw new JSONException(
                            String.format("Cannot find inner field %s. Check whether filter exists in backend", INNER_KEY));
                }
            }
            return filterIncludedFields;
        }

    }

    public static class QuestionResponseExtractor implements ResponseExtractor<QuestionId> {
        private static final String INNER_KEY = "question_id";

        @Override
        public List<QuestionId> extract(JSONArray itemArray) throws JSONException {
            List<QuestionId> questionIds = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);

                int id = item.getInt(INNER_KEY);
                questionIds.add(new QuestionId(id));
            }
            return questionIds;
        }
    }

    public static class PrivilegeResponseExtractor implements ResponseExtractor<PrivilegeShortDescription> {
        private static final String INNER_KEY = "short_description";

        @Override
        public List<PrivilegeShortDescription> extract(JSONArray itemArray) throws JSONException {
            List<PrivilegeShortDescription> privilegeShortDescriptions = new ArrayList<>();
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);

                String shortDescription = item.getString(INNER_KEY);
                privilegeShortDescriptions.add(new PrivilegeShortDescription(shortDescription));
            }
            return privilegeShortDescriptions;
        }
    }

    public static class QuestionId {
        private final int id;

        private QuestionId(int id) {
            this.id = id;
        }

        public boolean isValid(int placeHolder) {
            return id != placeHolder;
        }

        public int getId() {
            return id;
        }
    }

    public static class PrivilegeShortDescription {
        private final String shortDescription;

        private PrivilegeShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public String getShortDescription() {
            return shortDescription;
        }
    }

    public static class FilterIncludedFields {

        private final List<String> keys;

        private FilterIncludedFields(List<String> keys) {
            this.keys = keys;
        }

        public Set<String> getCommonKeySet() {
            Set<String> commonKeySet = new HashSet<>();
            for (String k : keys) {
                if (StackExchangeCommonWrapper.isCommonKey(k)) {
                    commonKeySet.add(StackExchangeCommonWrapper.getCommonKeyAsKey(k));
                }
            }
            return commonKeySet;
        }
    }
}
