package org.phdezann.cn.model.workflowy;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Node {
    private String id;
    private String nm;
    private long ct;
    private long lm;
    private Metadata metadata;
    private String no;
    @JsonManagedReference
    private List<Node> ch = new ArrayList<>();
    @JsonBackReference
    private Node parent;
}
