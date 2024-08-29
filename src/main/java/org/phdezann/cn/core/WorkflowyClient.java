package org.phdezann.cn.core;

import static org.phdezann.cn.model.workflowy.Config.ConfigKey.WORKFLOWY_SESSION_ID;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import org.phdezann.cn.model.workflowy.Config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WorkflowyClient {

    public static final String WORKFLOWY_URL = "https://workflowy.com/get_initialization_data?client_version=21&client_version_v2=28";

    public String getContent(Config config) {
        try {
            HttpRequest request = HttpRequest.newBuilder() //
                    .header("cookie", "sessionid=" + config.get(WORKFLOWY_SESSION_ID)) //
                    .header("cache-control", "no-cache") //
                    .uri(new URI(WORKFLOWY_URL)) //
                    .GET() //
                    .build();
            log.info("Downloading Workflowy data...");
            var body = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString()).body();
            log.info("Workflowy data downloaded");
            return body;
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class DownloadUrlResponse {
        private final int statusCode;
        private final String body;
    }

    public DownloadUrlResponse getDownloadUrl(Config config, long ownerId, String nodeId) {
        var url = String.format("https://workflowy.com/file-proxy/signed-original/%d/%s/?attempt=1", ownerId, nodeId);
        try {
            HttpRequest request = HttpRequest.newBuilder() //
                    .header("cookie", "sessionid=" + config.get(WORKFLOWY_SESSION_ID)) //
                    .header("cache-control", "no-cache") //
                    .uri(new URI(url)) //
                    .GET() //
                    .build();
            var send = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
            return new DownloadUrlResponse(send.statusCode(), send.body());
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InputStream download(Config config, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder() //
                    .header("cookie", "sessionid=" + config.get(WORKFLOWY_SESSION_ID)) //
                    .header("cache-control", "no-cache") //
                    .uri(new URI(url)) //
                    .GET() //
                    .build();
            return HttpClient.newBuilder().build().send(request, BodyHandlers.ofInputStream()).body();
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
