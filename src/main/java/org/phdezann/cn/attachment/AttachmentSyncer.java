package org.phdezann.cn.attachment;

import java.time.LocalDateTime;

import org.phdezann.cn.model.workflowy.Config;
import org.phdezann.cn.model.workflowy.Node;
import org.phdezann.cn.model.workflowy.Root;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AttachmentSyncer {

    private final AttachmentFinder attachmentFinder;
    private final DiskUpdater diskUpdater;
    private final SummaryPrinter summaryPrinter;

    public void sync(Config config, Root root, Node rootNode) {
        var fileNodes = attachmentFinder.findAll(rootNode);
        var now = LocalDateTime.now();
        diskUpdater.update(config, root, fileNodes, now);
        diskUpdater.removeOrphans(now);
        summaryPrinter.printSummary();
    }

}
