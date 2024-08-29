package org.phdezann.cn;

import org.phdezann.cn.attachment.AttachmentDownloader;
import org.phdezann.cn.attachment.AttachmentFinder;
import org.phdezann.cn.attachment.SummaryPrinter;
import org.phdezann.cn.attachment.AttachmentSyncer;
import org.phdezann.cn.attachment.DiskUpdater;
import org.phdezann.cn.attachment.PathBuilder;
import org.phdezann.cn.core.AppArgs;
import org.phdezann.cn.core.ConfigReader;
import org.phdezann.cn.core.JsonSerializer;
import org.phdezann.cn.core.TreeDeserializer;
import org.phdezann.cn.core.WorkflowyClient;

import com.beust.jcommander.JCommander;

public class Application {

    public static void main(String[] args) {
        AppArgs appArgs = new AppArgs();
        JCommander.newBuilder().addObject(appArgs).build().parse(args);

        var workflowyClient = new WorkflowyClient();
        var jsonDeserializer = new JsonSerializer();
        var configReader = new ConfigReader(appArgs, jsonDeserializer);
        var deserializer = new TreeDeserializer(jsonDeserializer);
        var attachmentFinder = new AttachmentFinder();
        var attachmentDownloader = new AttachmentDownloader(workflowyClient);
        var pathBuilder = new PathBuilder(appArgs);
        var diskUpdater = new DiskUpdater(appArgs, jsonDeserializer, pathBuilder, attachmentDownloader);
        var attachmentSummaryBuilder = new SummaryPrinter(appArgs, jsonDeserializer);
        var attachmentSyncer = new AttachmentSyncer(attachmentFinder, diskUpdater, attachmentSummaryBuilder);


        var config = configReader.read();
        var json = workflowyClient.getContent(config);
        var root = deserializer.deserializer(json);

        var node = deserializer.toWorkableNode(root);

        attachmentSyncer.sync(config, root, node);
    }

}
