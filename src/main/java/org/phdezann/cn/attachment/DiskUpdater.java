package org.phdezann.cn.attachment;

import static java.time.LocalDateTime.parse;
import static org.apache.commons.io.filefilter.TrueFileFilter.INSTANCE;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.phdezann.cn.attachment.PathBuilder.METADATA_FILENAME;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.phdezann.cn.core.AppArgs;
import org.phdezann.cn.core.JsonSerializer;
import org.phdezann.cn.core.NodeUtils;
import org.phdezann.cn.model.attachment.AttachmentMetadata;
import org.phdezann.cn.model.attachment.AttachmentNode;
import org.phdezann.cn.model.attachment.NodeFolder;
import org.phdezann.cn.model.workflowy.Config;
import org.phdezann.cn.model.workflowy.Node;
import org.phdezann.cn.model.workflowy.Root;
import org.phdezann.cn.support.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DiskUpdater {

    private final AppArgs appArgs;
    private final JsonSerializer jsonSerializer;
    private final PathBuilder pathBuilder;
    private final AttachmentDownloader attachmentDownloader;

    public void update(Config config, Root root, List<AttachmentNode> attachmentNodes, LocalDateTime now) {
        attachmentNodes.forEach(attachmentNode -> update(config, root, attachmentNode, now));
    }

    public void removeOrphans(LocalDateTime now) {
        listMetadataFiles(appArgs.getAttachmentsFolder()) //
                .stream() //
                .filter(metadataFile -> {
                    var attachmentMetadata = readMetadataFile(metadataFile).orElseThrow();
                    return !attachmentMetadata.getUpdated().isEqual(now);
                }).forEach(meta -> {
                    var dir = meta.getParentFile();
                    FileUtils.deleteDirectory(dir);
                    log.info("Removed orphan '{}'", dir);
                });
    }

    private void update(Config config, Root root, AttachmentNode attachmentNode, LocalDateTime now) {
        var nodePath = pathBuilder.buildNodeFolderPath(attachmentNode);
        var metadataPath = pathBuilder.buildMetadataFilePath(nodePath);
        var node = attachmentNode.getNode();
        var isOnDiskAndHasBeenModified = readMetadataFile(metadataPath)
                .map(persistedMetadata -> hasBeenModified(root, node, persistedMetadata)).orElse(true);
        var attachmentFolderPath = pathBuilder.buildAttachmentFolderPath(nodePath);
        var path = pathBuilder.buildAttachmentFilePath(attachmentFolderPath, node);
        if (!path.exists() || isOnDiskAndHasBeenModified) {
            FileUtils.deleteDirectory(attachmentFolderPath);
            FileUtils.forceMkdir(attachmentFolderPath);
            attachmentDownloader.download(config, root, node.getId(), path);
        }
        writeMetadataFile(metadataPath, toAttachmentMetadata(root, node, now));
    }

    private Optional<AttachmentMetadata> readMetadataFile(File dataFile) {
        if (!dataFile.exists()) {
            return Optional.empty();
        }
        var json = FileUtils.read(dataFile);
        var metadata = jsonSerializer.readValue(json, AttachmentMetadata.class);
        return Optional.of(metadata);
    }

    private void writeMetadataFile(File fullPath, AttachmentMetadata metadata) {
        FileUtils.write(fullPath, jsonSerializer.writeValue(metadata));
    }

    private boolean hasBeenModified(Root root, Node node, AttachmentMetadata persistedMetadata) {
        var lastModifiedReceived = toLocalDateTime(root, node.getLm());
        var lastModifiedStored = parse(persistedMetadata.getSelf().getUpdated());
        return lastModifiedReceived.isAfter(lastModifiedStored);
    }

    private AttachmentMetadata toAttachmentMetadata(Root root, Node node, LocalDateTime now) {
        var metadata = new AttachmentMetadata();
        metadata.setPath(joinPath(buildPathToRoot(node)));
        metadata.setSelf(toNodeFolder(root, node));
        metadata.setParent(toNodeFolder(root, node.getParent()));
        metadata.setUpdated(now);
        return metadata;
    }

    private String joinPath(List<String> path) {
        return String.join(" / ", path);
    }

    private List<String> buildPathToRoot(Node node) {
        var path = new ArrayList<String>();
        var current = node;
        while (true) {
            if (current.getParent() == null) {
                Collections.reverse(path);
                return path;
            }
            var id = NodeUtils.getShortId(current.getId());
            var title = defaultIfEmpty(trimToEmpty(current.getNm()), "(none)");
            path.add(String.format("[%s|%s]", id, title));
            current = current.getParent();
        }
    }

    private NodeFolder toNodeFolder(Root root, Node node) {
        var nodeFolder = new NodeFolder();
        nodeFolder.setId(node.getId());
        nodeFolder.setTitle(node.getNm());
        nodeFolder.setCreation(toISO(toLocalDateTime(root, node.getCt())));
        nodeFolder.setUpdated(toISO(toLocalDateTime(root, node.getLm())));
        nodeFolder.setNote(node.getNo());
        return nodeFolder;
    }

    private LocalDateTime toLocalDateTime(Root root, long value) {
        var joined = root.getProjectTreeData().getMainProjectTreeInfo().getDateJoinedTimestampInSeconds();
        var sinceEpoch = joined + value;
        return LocalDateTime //
                .ofInstant( //
                        Instant.ofEpochSecond(sinceEpoch), //
                        ZoneId.systemDefault());
    }

    private String toISO(LocalDateTime localDateTime) {
        return localDateTime.toString();
    }

    private List<File> listMetadataFiles(File dir) {
        return org.apache.commons.io.FileUtils //
                .listFiles(dir, new NameFileFilter(METADATA_FILENAME), INSTANCE) //
                .stream() //
                .sorted() //
                .toList();
    }

}
