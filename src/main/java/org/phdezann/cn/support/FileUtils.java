package org.phdezann.cn.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

public class FileUtils {

    public static void forceMkdir(File directory) {
        try {
            org.apache.commons.io.FileUtils.forceMkdir(directory);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void copy(InputStream in, File output) {
        try (var fos = new FileOutputStream(output)) {
            IOUtils.copy(in, fos);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String byteCountToDisplaySize(File file) {
        var size = org.apache.commons.io.FileUtils.sizeOf(file);
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }

    public static String read(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void write(File file, String content) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void deleteDirectory(File directory) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(directory);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Stream<Path> nioFilesList(Path dir) {
        try {
            return Files.list(dir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Stream<Path> nioFilesWalk(Path dir) {
        try {
            return Files.walk(dir, Integer.MAX_VALUE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
