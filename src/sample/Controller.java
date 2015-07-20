package sample;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class Controller implements Initializable {

    @FXML
    private TreeView topTreeView;
    @FXML
    private TreeView bottomTreeView;
    @FXML
    private Button copyBtn;

    private WorkerService mWorker;

    private ExecutorService doSomeShitExecutor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doSomeShitExecutor = Executors.newFixedThreadPool(5);
        TreeItem<String> topRootItem = new TreeItem<>("Top Tree Root");
        for (int i=0; i<3; ++i) {
            TreeItem<String> newItem = new TreeItem<>("Message: " + Integer.toString(i));
            topRootItem.getChildren().add(newItem);
        }
        topRootItem.setExpanded(true);
        topTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        topTreeView.setRoot(topRootItem);

        TreeItem<String> bottomRootItem = new TreeItem<>("Bottom Tree Root");
        bottomRootItem.setExpanded(true);
        bottomTreeView.setRoot(bottomRootItem);

        initializeWorker();
    }

    @FXML
    private void copyToBottom(ActionEvent actionEvent) {

        mWorker.restart();
        System.out.println("I'm finished: " + System.currentTimeMillis());
    }

    public void removeItems(ActionEvent actionEvent) {
        final ObservableList<TreeItem<String>> topSelectedItems = topTreeView.getSelectionModel().getSelectedItems();
        final ObservableList<TreeItem<String>> bottomItems = bottomTreeView.getRoot().getChildren();

        for (TreeItem<String> topTreeItem : topSelectedItems) {
            for (TreeItem<String> bottomTreeItem : bottomItems) {
                if (topTreeItem.getValue().equals(bottomTreeItem.getValue())) {
                    bottomItems.remove(bottomTreeItem);
                    break;
                }
            }
        }
    }

    void initializeWorker() {
        mWorker = new WorkerService(topTreeView.getSelectionModel().getSelectedItems());

        mWorker.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue instanceof ObservableList<?>) {
                    ObservableList<TreeItem<String>> newItemsList = (ObservableList<TreeItem<String>>) newValue;
                    System.out.println("New Items:");
                    bottomTreeView.getRoot().getChildren().addAll(newItemsList);
                }
            }
        });
        copyBtn.disableProperty().bind(mWorker.stateProperty().isEqualTo(Worker.State.RUNNING));
    }

    public void stop() {
        if (!mWorker.isShutdown()) {
            mWorker.shutdown();
        }
    }

    @FXML
    private void shutDownExecutor(ActionEvent actionEvent) {
        doSomeShitExecutor.shutdown();
    }
}
