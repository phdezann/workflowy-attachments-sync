package org.phdezann.cn.model.workflowy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Metadata {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class S3File {
        private boolean isFile;
        private String fileName;
        private String fileType;
        private String objectFolder;
        private boolean isAnimatedGIF;
        private long imageOriginalWidth;
        private long imageOriginalHeight;
    }

    private S3File s3File;
}
