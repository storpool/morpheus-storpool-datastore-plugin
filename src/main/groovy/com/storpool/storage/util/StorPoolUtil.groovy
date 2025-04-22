/*
* Copyright 2025 StorPool Storage AD
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.storpool.storage.util

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.storpool.storage.api.StorPoolSnapshotDef
import com.storpool.storage.api.StorPoolVolumeDef
import groovy.util.logging.Slf4j
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder

@Slf4j
class StorPoolUtil {
    private static final String OBJECT_DOES_NOT_EXIST = "objectDoesNotExist";
    private static final String DATA = "data";
    private static final String CLUSTERS = "clusters";

    static class StorPoolConnection {
        private String hostPort;
        private String token;
        private String templateName;

        StorPoolConnection(String url) {
            URI uri = new URI(url);
            if (!StringUtils.equalsIgnoreCase(uri.getScheme(), "storpool")) {
                throw new Exception("The scheme is invalid. The URL should be with a format storpool://{SP_AUTH_TOKEN}@{SP_API_HTTP}:{SP_API_HTTP_PORT}/{SP_TEMPLATE}");
            }
            hostPort = uri.getHost() + ":" + uri.getPort();
            token = uri.getUserInfo();
            templateName = uri.getPath().replace("/", "");
        }

        StorPoolConnection(String host, String port, String token, String templateName) {
            if (host == null || port == null || token == null || templateName == null) {
                throw new Exception("The host, port, token or the template name of the Data Storage is missing");
            }
            this.hostPort = host + ":" + port;
            this.token = token;
            this.templateName = templateName
        }

        String getHostPort() {
            return this.hostPort;
        }

        String getAuthToken() {
            return this.token;
        }

        String getTemplateName() {
            return this.templateName;
        }
    }
    public static class SpApiResponse {
        private SpApiError error;
        public JsonElement fullJson;

        public SpApiResponse() {
        }

        public SpApiError getError() {
            return this.error;
        }

        public void setError(SpApiError error) {
            this.error = error;
        }
    }

    static final class SpApiError {
        private String name;
        private String descr;

        public SpApiError() {
        }

        public String getName() {
            return this.name;
        }

        public String getDescr() {
            return this.descr;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public String toString() {
            return String.format("%s: %s", name, descr);
        }
    }
    private static SpApiResponse spApiRequest(HttpRequestBase req, String query, StorPoolConnection conn) {

        if (conn == null)
            conn = new StorPoolConnection("");

        if (conn.getHostPort() == null) {
            throw new Exception("Invalid StorPool config. Missing SP_API_HTTP_HOST");
        }

        if (conn.getAuthToken() == null) {
            throw new Exception("Invalid StorPool config. Missing SP_AUTH_TOKEN");
        }

        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            def (JsonElement el, SpApiResponse apiResp) = executeReq(conn, query, req, httpclient)
            apiResp.fullJson = el;
            return apiResp;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private static List executeReq(StorPoolConnection conn, String query, HttpRequestBase req, CloseableHttpClient httpclient) {
        final String qry = String.format("http://%s/ctrl/1.0/%s", conn.getHostPort(), query);
        final URI uri = new URI(qry);

        log.info("Query {}", qry);
        req.setURI(uri);
        req.setHeader("Authorization", String.format("Storpool v1:%s", conn.getAuthToken()));
        log.info("Headers {}", req.getAllHeaders().toArrayString());

        final HttpResponse resp = httpclient.execute(req);

        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));

        JsonElement el = JsonParser.parseReader(br);

        SpApiResponse apiResp = gson.fromJson(el, SpApiResponse.class);
        [el, apiResp]
    }

    private static SpApiResponse GET(String query, StorPoolConnection conn) {
        return spApiRequest(new HttpGet(), query, conn);
    }

    private static SpApiResponse POST(String query, Object json, StorPoolConnection conn) {
        HttpPost req = new HttpPost();
        if (json != null) {
            Gson gson = new Gson();
            String js = gson.toJson(json);
            StringEntity input = new StringEntity(js, ContentType.APPLICATION_JSON);
            log.info("Request:" + js);
            req.setEntity(input);
        }

        return spApiRequest(req, query, conn);
    }

    private static boolean objectExists(SpApiError err) {
        if (!err.getName().equals(OBJECT_DOES_NOT_EXIST)) {
            throw new Exception(err.getDescr());
        }
        return false;
    }

    static boolean templateExists(StorPoolConnection conn) {
        SpApiResponse resp = GET("VolumeTemplateDescribe/" + conn.getTemplateName(), conn);
        return resp.getError() == null ? true : objectExists(resp.getError());
    }

    static JsonArray templatesStats(StorPoolConnection conn, String templateName) {
        SpApiResponse resp = GET("MultiCluster/AllClusters/VolumeTemplatesStatus", conn);
        JsonObject obj = resp.fullJson.getAsJsonObject();
        return obj.getAsJsonObject(DATA).getAsJsonArray(CLUSTERS);
    }

    static SpApiResponse volumeCreate(StorPoolSnapshotDef volume, StorPoolConnection conn) {
        return POST("MultiCluster/VolumeCreate", volume, conn);
    }

    static SpApiResponse volumeUpdate(StorPoolVolumeDef volume, StorPoolConnection conn) {
        return POST("MultiCluster/VolumeUpdate/" + volume.getName(), volume, conn);
    }

    static SpApiResponse volumeSnapshot(StorPoolSnapshotDef snapshot, StorPoolConnection conn) {
        return POST("MultiCluster/VolumeSnapshot/" + snapshot.getVolumeName(), snapshot, conn);
    }
}
