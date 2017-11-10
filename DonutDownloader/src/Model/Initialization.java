package Model;

import View.View;
public class Initialization {
    private View view;
    private Model model;
    public Initialization(Model model, View view){
        this.model = model;
        this.view = view;
    }
    public void login() {
        this.view.send.setOnAction(event -> {
            boolean canLogIn = model.checkLogin(view.loginField.getText(),view.passwordField.getText());
            System.out.println(canLogIn);
            if(canLogIn){
                view.loginStage.close();
                view.dialog.show();
                view.setVisible(true);
               /* new Updater*///TODO Updater Class
            }
            else System.out.println("LOGGING FAILED");
        });
    }
}
