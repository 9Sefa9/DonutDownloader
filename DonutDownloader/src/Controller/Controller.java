package Controller;

import Model.Model;
import View.View;
import Model.Initialization;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
public class Controller {
    private Model model;
    private View view;
    public Controller(){

        //link(model,view);
    }
    public Model getModel(){
        return this.model;
    }
    public View getView(){
        return this.view;
    }
    public void link(Model model, View view) {
        this.model = model;
        this.view = view;
        /*TODO Übertragen des True links auf den tatsächlichen link und die Initialization Funktion in einen eigene Threadklasse. Und
        * am besten einen CORRECT INVALID Text Feld.   TextHovering Farbe ändern */

        //CHECK LOGIN AND UPDATE
        new Initialization(getModel(),getView()).createInstance();
        try {

            //UPDATE
            new UpdaterClass(getModel(), getView());
            this.view.paste.setOnAction(e -> this.model.ctrlv(this.view.insertUrl));
            this.view.convert.setOnAction(e -> this.model.setUrlToList(this.view.insertUrl.getText(), this.view.listViewConvertList));
            this.view.download.setOnAction(e -> this.model.savePath());
            this.view.addsongs.setOnAction(e -> this.model.processDownloadFromList(this.view.listViewConvertList, this.view.listViewDownloadList));
            this.view.goToPath.setOnAction(e -> this.model.openSavepathExplorer());
            this.view.listViewDownloadList.setOnMouseClicked(event -> {
                this.view.title.setText(this.view.listViewDownloadList.getSelectionModel().getSelectedItem());
            });

            this.view.title.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    if (ke.getCode().equals(KeyCode.ENTER)) {
                        if (!view.listViewDownloadList.getSelectionModel().isEmpty() && !view.listViewDownloadList.getSelectionModel().getSelectedItem().startsWith("Downloading")) {
                            SimpleStringProperty defaultTitle = new SimpleStringProperty(view.listViewDownloadList.getSelectionModel().getSelectedItem());
                            view.listViewDownloadList.getItems().remove(view.listViewDownloadList.getSelectionModel().getSelectedItem());
                            view.listViewDownloadList.getItems().add(view.title.getText());

                            model.replaceTitleWith(defaultTitle.get(), view.title.getText());
                        } else System.out.println("nothing to change..");
                    }
                }
            });
            this.view.deletesong.setOnAction(e -> {
                if (!this.view.listViewDownloadList.getSelectionModel().getSelectedItem().isEmpty()) {
                    this.view.listViewDownloadList.getItems().remove(this.view.listViewDownloadList.getSelectionModel().getSelectedItem());
                }
            });
        } catch (Exception i) {
            i.printStackTrace();
        }

        this.view.whatsNew.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.showWhatsNew(view);
            }
        });

    }
}
class UpdaterClass extends Thread{
    Model model;
    View view;
    public UpdaterClass(Model model, View view){
        this.view = view;
        this.model = model;
        run();
    }
    public void run() {
        try {
            if (this.model.hasUpdate()) {

                this.view.midframe.setVisible(false);
                this.view.upperframe.setVisible(false);
                this.view.rightframe.setVisible(false);
                this.view.downframe.setVisible(false);

                this.view.dialog.show();
                this.model.processUpdate();

            } else Platform.runLater(() -> this.view.dialog.close());

        } catch (Exception e) {
            System.out.println("CONNECTION REFUSED!");
            e.printStackTrace();
        }
    }

}

