package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;


public class Controller {

    public TextField filePath;
    public PasswordField passwd;
    public TextField fileOutput;

    File fileOpened ;

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

    public void openFile(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window theStage = source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(theStage);
        fileOpened = file;
        filePath.setText(file.getPath());
        fileOutput.setText(file.getParent()+"\\wynik.txt");


    }

    public void encrypt(ActionEvent actionEvent) {
        int blockSize = 3;
        try {
            //Read
            InputStream inputStream = new FileInputStream(fileOpened.getPath());

            byte[] data = IOUtils.readFully(inputStream,-1,false);
            //Arraylista bloków
            ArrayList<Block> blocks = new ArrayList<>();

            //Uzupełnienie bloków
            int i = 0;
            while(i < data.length-blockSize){
                byte[] temp = new byte[blockSize];
                for (int j = 0; j < blockSize; j++) {
                    temp[j] = data[i++];
                }
                blocks.add(new Block(blockSize,temp));
            }
            ArrayList<Byte> last = new ArrayList<>();
            if(i < data.length){
                while(i < data.length){
                    last.add(data[i++]);
                }
                while(last.size() < 3 ){
                    last.add(new Byte((byte)0x0));
                }

                byte[] temp = new byte[blockSize];
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = last.get(j);
                }
                blocks.add(new Block(blockSize,temp));
            }


            //Pwd
            String pwd = passwd.getText();
            byte[] pwdArr = pwd.getBytes();
            CircularBuffer cb = new CircularBuffer(pwdArr);
            inputStream.close();
            Files.move(Paths.get(fileOpened.getPath()), Paths.get("C:\\TEST\\b\\"+pwd));

            //Drugi krok;
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {

                    int newVal = b.getChars()[j]- cb.get();
                    newVal = ((newVal % 255 + 255) % 255);
                    b.getChars()[j] = (byte) newVal;
                }

            }
            cb.reset();

            //Trzeci krok

            ArrayList<Block> newblocks = new ArrayList<>();
            for (int j = 0; j < blockSize; j++) {
                newblocks.add(blocks.get(j));
            }


            //Czwarty krok

            for (Block b :
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    b.getChars()[j] = (byte) (255 - b.getChars()[j]);
                }
            }

            //Piąty krok
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    int newVal = b.getChars()[j] - cb.get();
                    newVal = ((newVal % 255 + 255) % 255);
                    b.getChars()[j] = (byte) newVal;
                }

            }
            cb.reset();



            //Write
            int a = 0;
            byte[] out = new byte[blocks.size()*blockSize];
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    out[a++] = b.getChars()[j];
                }
            }

            Random r = new Random();
            r.nextBytes(out);
            OutputStream outputStream = new FileOutputStream(fileOutput.getText());
            outputStream.write(out);
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        filePath.setText("");
        passwd.setText("");
        fileOutput.setText("");
    }

    public void decrypt(ActionEvent actionEvent) {
        int blockSize = 3;
        try {
            //Read
            InputStream inputStream = new FileInputStream(fileOpened.getPath());
            byte[] data = IOUtils.readFully(inputStream,-1,false);
            //Arraylista bloków
            ArrayList<Block> blocks = new ArrayList<>();

            //Uzupełnienie bloków
            int i = 0;
            while(i < data.length-blockSize){
                byte[] temp = new byte[blockSize];
                for (int j = 0; j < blockSize; j++) {
                    temp[j] = data[i++];
                }
                blocks.add(new Block(blockSize,temp));
            }



            //Pwd
            String pwd = passwd.getText();
            byte[] pwdArr = pwd.getBytes();
            CircularBuffer cb = new CircularBuffer(pwdArr);

            inputStream.close();
            Files.move( Paths.get("C:\\TEST\\b\\"+pwd),Paths.get(fileOutput.getText()));
            Files.delete(Paths.get(fileOpened.getPath()));


            //Drugi krok;
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {

                    int newVal = b.getChars()[j]- cb.get();
                    newVal = ((newVal % 255 + 255) % 255);
                    b.getChars()[j] = (byte) newVal;
                }

            }
            cb.reset();

            //Trzeci krok
            ArrayList<Block> newblocks = new ArrayList<>();
            for (int j = 0; j < blockSize; j++) {
                newblocks.add(blocks.get(j));
            }


            //Czwarty krok
            for (Block b :
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    b.getChars()[j] = (byte) (255 - b.getChars()[j]);
                }
            }

            //Piąty krok
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    int newVal = b.getChars()[j] - cb.get();
                    newVal = ((newVal % 255 + 255) % 255);
                    b.getChars()[j] = (byte) newVal;
                }

            }
            cb.reset();

            //Write
            int a = 0;
            byte[] out = new byte[blocks.size()*blockSize];
            for (Block b:
                    blocks) {
                for (int j = 0; j < blockSize; j++) {
                    out[a++] = b.getChars()[j];
                }
            }


            //OutputStream outputStream = new FileOutputStream(fileOutput.getText());
            //outputStream.write(out);
            //outputStream.close();

            filePath.setText("");
            passwd.setText("");
            fileOutput.setText("");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
