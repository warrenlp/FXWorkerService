package sample.threads;

import javafx.scene.control.TreeItem;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Created by warre_000 on 7/29/2015.
 */
public class TreeItemStringStripperCallable implements Callable<String> {

    private WeakReference<TreeItem<String>> mLocalTreeItemRef;

    public TreeItemStringStripperCallable(TreeItem<String> treeItem) {
        mLocalTreeItemRef = new WeakReference<>(treeItem);
    }

    @Override
    public String call() throws Exception {

        TreeItem<String> localTreeItem = mLocalTreeItemRef.get();
        String localTreeItemString = null;
        if (localTreeItem != null) {
            // Simulate long process
            Thread.sleep((long)(Math.random() + 0.5) * 2000);
            localTreeItemString = localTreeItem.getValue();
        }

        return localTreeItemString;
    }
}
