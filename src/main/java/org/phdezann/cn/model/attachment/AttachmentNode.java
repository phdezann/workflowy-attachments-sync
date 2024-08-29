package org.phdezann.cn.model.attachment;

import org.phdezann.cn.model.workflowy.Node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AttachmentNode {
    private final String path;
    private final Node node;
}
