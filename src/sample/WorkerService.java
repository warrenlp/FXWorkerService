package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import sample.threads.LeakedExceptionRunnable;
import sample.threads.NamedThreadsFactory;
import sample.threads.ScheduledThreadPoolExecutorAfterExecute;
import sample.threads.TreeItemStringStripperCallable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by warre_000 on 7/19/2015.
 */
public class WorkerService extends Service<List<String>> {

    ExecutorService executorService;
//    ScheduledExecutorService scheduledExecutorService;
    CompletionService<String> completionService;
    ObservableList<TreeItem<String>> mSelectedList;
    ObjectProperty<ObservableList<TreeItem<String>>> mSelectedTreeItemListProp;

    public WorkerService(ObservableList<TreeItem<String>> selectedList) {
        executorService = Executors.newFixedThreadPool(5);
//        scheduledExecutorService = ScheduledThreadPoolExecutorAfterExecute.newSingleThreadScheduledExecutor(new NamedThreadsFactory());
////        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadsFactory());
//        scheduledExecutorService.scheduleAtFixedRate(new LeakedExceptionRunnable()
//                , 0
//                , 2, TimeUnit.SECONDS);
        completionService = new ExecutorCompletionService<>(executorService);
        mSelectedTreeItemListProp = new SimpleObjectProperty<>();
        mSelectedList = selectedList;
        mSelectedTreeItemListProp.bind(Bindings.createObjectBinding(() -> mSelectedList, mSelectedList));
    }

    public boolean isShutdown() {
        return (executorService.isShutdown());// && scheduledExecutorService.isShutdown());
    }

    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
//        if (!scheduledExecutorService.isShutdown()) {
//            scheduledExecutorService.shutdown();
//        }
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {

            @Override
            protected List<String> call() throws Exception {
                final ObservableList<TreeItem<String>> observableList = mSelectedTreeItemListProp.get();
                final int total = observableList.size();
                updateProgress(0, total);

                for (TreeItem<String> treeItem : observableList) {
                    completionService.submit(new TreeItemStringStripperCallable(treeItem));
                }

                List<String> newItemsList = new ArrayList<>();
                for (int i=1; i<=total; ++i) {
                    Future<String> treeItemFuture = completionService.take();
                    String localString = treeItemFuture.get();
                    newItemsList.add(localString);
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
