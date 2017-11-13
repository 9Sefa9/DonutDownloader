package Model;

import View.View;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class Initialization {
    private View view;
    private Model model;
    public Initialization(Model model, View view){
        this.model = model;
        this.view = view;
        if(!model.isConnectedToLoginServer()){
            //löscht alle elemente bis auf die anzeige, dass es keine Server gibt.
            view.loginPane.getChildren().remove(0,3);
            view.info.setText("SERVER OFFLINE! \nPlease contact the developer \nof this Application!");
            view.info.setFill(Color.DARKRED);
        }else{
            view.info.setText("SERVER\nONLINE!");
            view.info.setFill(Color.DARKGREEN);
        }
    }
    public void createInstance() {
        this.view.passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                 action();
                }
            }
        });
        this.view.send.setOnAction(event -> {
           action();
        });
    }
    public void action(){
        boolean canLogIn = model.checkLogin(view.loginField.getText(),view.passwordField.getText());
        System.out.println(canLogIn);
        if(canLogIn){
            view.dialog.show();
            view.setVisible(true);
            view.loginStage.close();

            //TODO Raspberry PI , LoginServer Funktioniert aufgrund eines Bugs nicht!
            //UPDATE
            new Update(model, view);
        }
        else {
            if(!model.isConnectedToLoginServer()){
                //löscht alle elemente bis auf die anzeige, dass es keine Server gibt.
                view.loginPane.getChildren().remove(0,3);
                view.info.setText("SERVER OFFLINE! \nPlease contact the developer \nof this Application!");
                view.info.setFill(Color.DARKRED);
            }else {
                view.info.setText("LOGIN\nFAILED");
                view.info.setFill(Color.LIGHTCORAL);
            }
        }
    }
}
