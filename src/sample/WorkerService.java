package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by warre_000 on 7/19/2015.
 */
public class WorkerService extends Service<ObservableList<TreeItem<String>>> {

    ExecutorService executorService;
    CompletionService<TreeItem<String>> completionService;
    ObservableList<TreeItem<String>> mSelectedList;
    ObjectProperty<ObservableList<TreeItem<String>>> mSelectedTreeItemListProp;

    public WorkerService(ObservableList<TreeItem<String>> selectedList) {
        executorService = Executors.newFixedThreadPool(5);
        completionService = new ExecutorCompletionService<>(executorService);
        mSelectedTreeItemListProp = new SimpleObjectProperty<>();
        mSelectedList = selectedList;
        mSelectedTreeItemListProp.bind(Bindings.createObjectBinding(() -> mSelectedList, mSelectedList));
    }

    public boolean isShutdown() { return executorService.isShutdown(); }

    public void shutdown() { executorService.shutdown(); }

    @Override
    protected Task<ObservableList<TreeItem<String>>> createTask() {
        return new Task<ObservableList<TreeItem<String>>>() {

            @Override
            protected ObservableList<TreeItem<String>> call() throws Exception {
                final ObservableList<TreeItem<String>> observableList = mSelectedTreeItemListProp.get();
                final int total = observableList.size();
                updateProgress(0, total);

                for (TreeItem<String> treeItem : observableList) {
                    DoSomeShitCallable doSomeShitCallable = new DoSomeShitCallable(treeItem);
                    completionService.submit(doSomeShitCallable);
                }

                ObservableList<TreeItem<String>> newItemsList = FXCollections.observableArrayList();
                for (int i=1; i<=total; ++i) {
                    Future<TreeItem<String>> treeItemFuture = completionService.take();
                    TreeItem<String> localItem = treeItemFuture.get();
                    newItemsList.add(localItem);
                    updateProgress(i, total);
                }

                return newItemsList;
            }
        };
    }
    private static class DoSomeShitCallable implements Callable<TreeItem<String>> {
        TreeItem<String> mTreeItem;

        DoSomeShitCallable(TreeItem<String> treeItem) {
            this.mTreeItem = treeItem;
        }

        @Override
        public TreeItem<String> call() {
            TreeItem<String> localTreeItem = new TreeItem<>(mTreeItem.getValue());

            try {
                Thread.sleep((long)(Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            System.out.println("Original Item");
            System.out.println(mTreeItem.hashCode());
            System.out.println("New Item");
            System.out.println(localTreeItem.hashCode());

            System.out.println("I'm finally done: " + System.currentTimeMillis());
            return localTreeItem;
        }
    }

}
