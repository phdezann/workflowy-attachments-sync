package org.phdezann.cn.model.attachment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentMetadata {
    private String path;
    private NodeFolder self;
    private NodeFolder parent;
    private LocalDateTime updated;
}
