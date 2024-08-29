package org.phdezann.cn.model.workflowy;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Main {
    private String rootProject;
    private List<Node> rootProjectChildren = new ArrayList<>();
    private long ownerId;
    private long dateJoinedTimestampInSeconds;
}
