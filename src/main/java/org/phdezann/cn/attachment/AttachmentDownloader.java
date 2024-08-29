package org.phdezann.cn.attachment;

import static org.phdezann.cn.support.FileUtils.byteCountToDisplaySize;

import java.io.File;

import org.phdezann.cn.core.WorkflowyClient;
import org.phdezann.cn.model.workflowy.Config;
import org.phdezann.cn.model.workflowy.Root;
import org.phdezann.cn.support.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AttachmentDownloader {

    final private WorkflowyClient workflowyClient;

    public void download(Config config, Root root, String nodeId, File output) {
        var response = workflowyClient.getDownloadUrl(config, getOwnerId(root), nodeId);
        if (response.getStatusCode() != 200) {
            log.info("Skipping download of {} as we are unable to get the download url", nodeId);
            return;
        }
        var url = extractUrl(response.getBody());
        var in = workflowyClient.download(config, url);
        FileUtils.copy(in, output);
        log.info("Downloaded {} of size {}", output.getName(), byteCountToDisplaySize(output));
    }

    private long getOwnerId(Root root) {
        return root.getProjectTreeData().getMainProjectTreeInfo().getOwnerId();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class DownloadToken {
        private String url;
    }

    private String extractUrl(String json) {
        try {
            var objectMapper = new ObjectMapper();
            var downloadToken = objectMapper.readValue(json, DownloadToken.class);
            return downloadToken.getUrl();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
