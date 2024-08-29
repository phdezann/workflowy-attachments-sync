package org.phdezann.cn.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppArgs {

    @Parameter(names = "--config-file", required = true)
    private List<File> configFiles = new ArrayList<>();

    @Parameter(names = "--attachments-folder")
    private File attachmentsFolder;

}
