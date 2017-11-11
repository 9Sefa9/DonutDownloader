package Model;

import View.View;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class Update {
    private Model model;
    private View view;
    public Update(Model model, View view){
        this.view = view;
        this.model = model;
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
            System.out.println("UPDATE ISSUES, CONNECTION REFUSED");
            //e.printStackTrace();
        }
    }
}
