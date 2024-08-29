package org.phdezann.cn.core;

import org.phdezann.cn.model.workflowy.Node;
import org.phdezann.cn.model.workflowy.Root;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TreeDeserializer {

    private final JsonSerializer jsonSerializer;

    public Root deserializer(String json) {
        return jsonSerializer.readValue(json, Root.class);
    }

    public Node toWorkableNode(Root root) {
        var node = new Node();
        var rootProjectChildren = root.getProjectTreeData().getMainProjectTreeInfo().getRootProjectChildren();
        rootProjectChildren.forEach(n -> n.setParent(node));
        node.setId("00000000-0000-0000-0000-00000000");
        node.getCh().addAll(rootProjectChildren);
        return node;
    }

}
