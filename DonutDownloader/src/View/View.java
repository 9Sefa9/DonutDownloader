package View;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class View extends BorderPane{
   public ListView<String> listViewConvertList, listViewDownloadList;
    public Button addsongs,deletesong,download, whatsNew, send;
    public GridPane upperframe,downframe,rightframe,midframe,loginPane;
    public TextField title;
    public Text titleText,titleForConvertList,titleForDownloadList,info;
    public TextField insertUrl,loginField;
    public PasswordField passwordField;
    public Button paste,convert;
    public Text tutorial;
    public Button goToPath;
    public Alert login,dialog,whatsNewDialog;
    public Stage loginStage,dialogStage,whatsNewStage;
    public View(){

        //Initialization System
        login = new Alert(Alert.AlertType.NONE);
        login.setHeaderText("\t       Login");
        login.setWidth(500);
        login.setHeight(450);
        login.setTitle("Login");

        loginStage = (Stage) login.getDialogPane().getScene().getWindow();

        loginField = new TextField();
        loginField.setPromptText("Username...");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password...");

        send = new Button("Send");

        info = new Text();
        info.setFill(Color.RED);

        loginPane = new GridPane();
        loginPane.setVgap(2);
        loginPane.add(loginField,5,0);
        loginPane.add(passwordField,5,10);
        loginPane.add(send,5,20);
        loginPane.add(info,6,20);
        login.getDialogPane().contentProperty().set(loginPane);
        loginStage.setOnCloseRequest(e ->Platform.exit());
        loginStage.setAlwaysOnTop(true);
        loginStage.show();


            //Update
            dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setHeaderText(null);
            dialog.setWidth(500);
            dialog.setHeight(450);
            dialog.setTitle("Update...");
            dialog.setContentText("Please do not close. It is updating automatically.\nThe newer version is available in the same folder as this application.\n");
            dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.setAlwaysOnTop(true);

            //Midframe
            midframe = new GridPane();
            midframe.setVgap(30);
            midframe.setHgap(20);

            titleForConvertList = new Text("        MP3 Download List");
            titleForConvertList.setFont(new Font("Verdana", 15));
            midframe.add(titleForConvertList, 1, 1);

            titleForDownloadList = new Text("       Downloaded Files");
            titleForDownloadList.setFont(new Font("Verdana", 15));
            midframe.add(titleForDownloadList, 2, 1);

            listViewConvertList = new ListView<>();
            listViewConvertList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listViewConvertList.setPrefHeight(500);
            listViewConvertList.setStyle("-fx-font: 14px Verdana");
            midframe.add(listViewConvertList, 1, 2);

            listViewDownloadList = new ListView<>();
            listViewDownloadList.setPrefHeight(500);
            listViewDownloadList.setStyle("-fx-font: 14px Verdana");
            midframe.add(listViewDownloadList, 2, 2);


            //downframe
            downframe = new GridPane();
            downframe.setVgap(30);
            downframe.setHgap(30);

            addsongs = new Button("Start Download");
            addsongs.setPadding(new Insets(10, 10, 10, 10));
            downframe.add(addsongs, 2, 0);

            goToPath = new Button("Open Download Path");
            goToPath.setPadding(new Insets(10, 10, 10, 10));
            downframe.add(goToPath, 8, 0);

            whatsNew = new Button("Whats New ?");
            whatsNew.setPadding(new Insets(10, 10, 10, 10));
            downframe.add(whatsNew, 13, 0);

            //upperframe
            upperframe = new GridPane();
            insertUrl = new TextField();
            insertUrl.setPromptText("Copy/paste your Youtube link here...");
            insertUrl.setPadding(new Insets(10, 10, 10, 10));
            insertUrl.setPrefWidth(500);
            insertUrl.setStyle("-fx-prompt-text-fill: darkorange");
            upperframe.add(insertUrl, 1, 2);

            paste = new Button("Paste");
            paste.setPadding(new Insets(10, 10, 10, 10));
            upperframe.add(paste, 2, 2);

            convert = new Button("Add to MP3 Download List");
            convert.setPadding(new Insets(10, 20, 10, 10));
            upperframe.add(convert, 3, 2);
            upperframe.setHgap(5);

            //rightframe
            rightframe = new GridPane();
            rightframe.setMinSize(200, 500);

            titleText = new Text("Title");
            rightframe.add(titleText, 0, 9);

            title = new TextField();
            title.setPadding(new Insets(5, 20, 5, 5));
            rightframe.add(title, 0, 10);

            download = new Button("Set Download Path");
            download.setPadding(new Insets(10, 10, 10, 10));
            rightframe.add(download, 0, 14);

            deletesong = new Button("Delete Song from list");
            deletesong.setPadding(new Insets(10, 0, 10, 0));
            deletesong.setPrefWidth(200);
            rightframe.add(deletesong, 0, 18);

            tutorial = new Text();
            tutorial.setFont(Font.font(20));
            tutorial.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_LIGHT, 15));
            tutorial.setText(" Tutorial\n\n1.Set Download Path\n\n2.Paste Youtube link\n\n3.Add to Download list\n\n4.Start Download\n\n5.MP3-Title=Download done!");
            rightframe.add(tutorial, 0, 21);
            rightframe.setHgap(10);
            rightframe.setVgap(10);

            rightframe.getColumnConstraints().add(new ColumnConstraints(125));

                setRight(rightframe);
                setBottom(downframe);
                setTop(upperframe);
                setCenter(midframe);
                setVisible(false);

        }

}
