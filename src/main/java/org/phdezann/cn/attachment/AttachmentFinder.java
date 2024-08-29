package org.phdezann.cn.attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.phdezann.cn.core.NodeUtils;
import org.phdezann.cn.model.attachment.AttachmentNode;
import org.phdezann.cn.model.workflowy.Node;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttachmentFinder {

    public List<AttachmentNode> findAll(Node root) {
        var fileNodes = new ArrayList<AttachmentNode>();
        findAll(root, fileNodes);
        return fileNodes;
    }

    private void findAll(Node node, List<AttachmentNode> attachmentNodes) {
        var idPath = buildIdPath(node);
        var path = String.join("/", idPath);
        var hasAttachment = hasAttachment(node);
        if (hasAttachment) {
            attachmentNodes.add(new AttachmentNode(path, node));
        }
        node.getCh().forEach(root -> findAll(root, attachmentNodes));
    }

    private boolean hasAttachment(Node node) {
        var metadata = node.getMetadata();
        if (metadata == null) {
            return false;
        }
        var s3File = metadata.getS3File();
        return s3File != null;
    }

    private List<String> buildIdPath(Node node) {
        var path = new ArrayList<String>();
        buildIdPath(node, path);
        Collections.reverse(path);
        return path;
    }

    private void buildIdPath(Node node, List<String> path) {
        path.add(NodeUtils.getShortId(node.getId()));
        var parent = node.getParent();
        if (parent == null) {
            return;
        }
        buildIdPath(parent, path);
    }

}
