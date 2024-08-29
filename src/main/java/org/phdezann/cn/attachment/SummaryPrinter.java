package org.phdezann.cn.attachment;

import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.phdezann.cn.attachment.PathBuilder.ATTACHMENTS_FOLDER_NAME;
import static org.phdezann.cn.attachment.PathBuilder.METADATA_FILENAME;
import static org.phdezann.cn.support.FileUtils.nioFilesList;
import static org.phdezann.cn.support.FileUtils.nioFilesWalk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.phdezann.cn.core.AppArgs;
import org.phdezann.cn.core.JsonSerializer;
import org.phdezann.cn.model.attachment.AttachmentMetadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SummaryPrinter {

    private final AppArgs appArgs;
    private final JsonSerializer jsonSerializer;

    public void printSummary() {
        var attachmentsFolder = appArgs.getAttachmentsFolder();
        var attachments = walkDirectory(attachmentsFolder);

        log.info("Summary of attachment in {}", attachmentsFolder);
        attachments //
                .stream() //
                .sorted(Comparator.comparing(attachment -> attachment.getAttachmentMetadata().getPath())) //
                .forEach(attachment -> {
                    var filename = StringUtils.rightPad(abbreviate(attachment.getFile().getName(), 30), 15);
                    var displaySize = StringUtils.leftPad(attachment.getDisplaySize(), 10);
                    var path = attachment.getAttachmentMetadata().getPath();
                    var lastModified = attachment.getLastModified();
                    log.info("{} {} {} {}", filename, displaySize, lastModified, path);
                });

        var fileCount = attachments.size();
        var sizeOfAttachments = attachments //
                .stream() //
                .mapToLong(attachment -> FileUtils.sizeOf(attachment.getFile())) //
                .sum();
        var displaySize = FileUtils.byteCountToDisplaySize(sizeOfAttachments);
        log.info("Syncing done, {} files for about {}", fileCount, displaySize);
    }

    @RequiredArgsConstructor
    @Getter
    public static class AttachmentSummary {
        private final File file;
        private final String displaySize;
        private final String lastModified;
        private final AttachmentMetadata attachmentMetadata;
    }

    public Set<AttachmentSummary> walkDirectory(File root) {
        var rootAsPath = root.toPath();
        try (Stream<Path> stream = nioFilesWalk(rootAsPath)) {
            return stream //
                    .filter(Files::isDirectory) //
                    .filter(dir -> !dir.equals(rootAsPath))
                    .filter(dir -> StringUtils.equals(dir.getFileName().toString(), ATTACHMENTS_FOLDER_NAME))
                    .flatMap(dir -> {
                        var parent = dir.getParent().resolve(METADATA_FILENAME);
                        var attachmentMetadata = readMetadataFile(parent.toFile());
                        return getUniqueFile(dir)//
                                .map(file -> {
                                    var displaySize = FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file));
                                    var lastModified = attachmentMetadata.getSelf().getUpdated();
                                    return new AttachmentSummary(file, displaySize, lastModified, attachmentMetadata);
                                }).stream();
                    }).collect(Collectors.toSet());
        }
    }

    private Optional<File> getUniqueFile(Path dir) {
        var files = nioFilesList(dir).filter(file -> !Files.isDirectory(file)).collect(Collectors.toSet());
        if (files.size() > 1) {
            throw new RuntimeException("Only one file must be present per bullet");
        }
        if (files.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(files.iterator().next().toFile());
    }

    private AttachmentMetadata readMetadataFile(File dataFile) {
        var json = org.phdezann.cn.support.FileUtils.read(dataFile);
        return jsonSerializer.readValue(json, AttachmentMetadata.class);
    }

}
