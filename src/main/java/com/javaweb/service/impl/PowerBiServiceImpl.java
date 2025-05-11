package com.javaweb.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class PowerBiServiceImpl {

    @Value("${powerbi.client-id}")
    private String clientId;

    @Value("${powerbi.client-secret}")
    private String clientSecret;

    @Value("${powerbi.tenant-id}")
    private String tenantId;

    @Value("${powerbi.workspace-id}")
    private String workspaceId;

    @Value("${powerbi.report-id}")
    private String reportId;

    private static final String AUTHORITY = "https://login.microsoftonline.com/";
    private static final String SCOPE = "https://analysis.windows.net/powerbi/api/.default";
    private static final String API_URL = "https://api.powerbi.com/v1.0/myorg";

    public Map<String, Object> getEmbedInfo() throws Exception {
        // 1. Lấy access token từ Azure AD
        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                        clientId,
                        ClientCredentialFactory.createFromSecret(clientSecret))
                .authority(AUTHORITY + tenantId + "/")
                .build();

        ClientCredentialParameters params = ClientCredentialParameters.builder(
                        Collections.singleton(SCOPE))
                .build();

        IAuthenticationResult authResult = app.acquireToken(params).join();
        String accessToken = authResult.accessToken();

        // 2. Gọi Power BI REST API để tạo embed token
        String url = String.format("%s/groups/%s/reports/%s/GenerateToken",
                API_URL, workspaceId, reportId);
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity("{\"accessLevel\":\"View\"}", StandardCharsets.UTF_8));

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Power BI API Error: " + response.getStatusLine());
        }

        String json = EntityUtils.toString(response.getEntity());

        // 3. Parse token từ JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String embedToken = root.path("token").asText();

        // 4. Tạo embed URL
        String embedUrl = String.format("%s/groups/%s/reports/%s/ReportSection",
                API_URL, workspaceId, reportId);

        Map<String, Object> info = new HashMap<>();
        info.put("embedToken", embedToken);
        info.put("embedUrl", embedUrl);
        info.put("reportId", reportId);
        return info;
    }
}
