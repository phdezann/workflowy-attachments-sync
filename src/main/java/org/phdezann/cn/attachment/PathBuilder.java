package org.phdezann.cn.attachment;

import java.io.File;

import org.phdezann.cn.core.AppArgs;
import org.phdezann.cn.model.attachment.AttachmentNode;
import org.phdezann.cn.model.workflowy.Node;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathBuilder {

    public static final String METADATA_FILENAME = "metadata.json";
    public static final String ATTACHMENTS_FOLDER_NAME = "attachments";

    private final AppArgs appArgs;

    public File buildNodeFolderPath(AttachmentNode attachmentNode) {
        return new File(appArgs.getAttachmentsFolder(), attachmentNode.getPath());
    }

    public File buildMetadataFilePath(File nodePath) {
        return new File(nodePath, METADATA_FILENAME);
    }

    public File buildAttachmentFolderPath(File nodePath) {
        return new File(nodePath, ATTACHMENTS_FOLDER_NAME);
    }

    public File buildAttachmentFilePath(File attachmentFolderPath, Node node) {
        var fileName = node.getMetadata().getS3File().getFileName();
        return new File(attachmentFolderPath, fileName);
    }
}
