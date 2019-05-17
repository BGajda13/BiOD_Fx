package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller {
    String help = "Tekst pomocy";
    public void showHelp(ActionEvent actionEvent) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Node source = (Node) actionEvent.getSource();
        Window theStage = source.getScene().getWindow();
        dialog.initOwner(theStage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text(help));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
