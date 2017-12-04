package Model;

import MP3.MP3;
import View.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class Model {
    public static int ID = 17;
    public Socket client = null;
    public DirectoryChooser dirChooser;
    public File fileSave;
    public ListView<String> downloadList, convertList;
    public DataInputStream disFromServer;
    public DataOutputStream dosFromServer, dosFromLocal;
    public Path currentPath;
    public int serverID;
    public String choosenPath, currentLocation;
    public ObservableList<String> urllist;
    private String[] connectionList ={"rudralovesparo.ddns.net"};  // kann erweitert werden.. optional!

    public Model() throws Exception {
        //remove old versions
        deletePreviousVersions();
        //mp3 initialisierung
        urllist = FXCollections.observableArrayList();
    }

    //attached to add to mp3 list

    public void setUrlToList(String url, ListView<String> downloadList) {

        if (!url.isEmpty() && url.startsWith("https://www.youtube")) {
            try {

                for (int i = 0; i < downloadList.getItems().size(); i++) {
                    if (downloadList.getItems().get(i).equals(url)) {
                        return;
                    }
                }


                this.urllist.add(url);
                downloadList.getItems().add(url);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void processDownloadFromList(ListView<String> convertList, ListView<String> downloadList) {
        this.downloadList = downloadList;
        this.convertList = convertList;
        if (this.choosenPath != null && !this.choosenPath.equals("")) {

            for (String url : this.urllist) {
                this.downloadList.getItems().add("Downloading " + url);
                this.convertList.getItems().remove(url);
            }

            new innerProcessClass(this.choosenPath, this.urllist, downloadList, convertList).start();
            this.urllist = FXCollections.observableArrayList();


        } else {
            savePath();
            processDownloadFromList(convertList, downloadList);
        }
    }

    //attached to paste
    public void ctrlv(TextField urlfield) {
        urlfield.setText(null);
        urlfield.paste();

        if (!urlfield.getText().startsWith("https://www.youtube")) {
            urlfield.setText(null);
            urlfield.setStyle("-fx-prompt-text-fill: red");
            urlfield.setPromptText("Copied URL is not a valid Youtube link !");
        }
    }

    //attached to save path
    public void savePath() {
        try {
            dirChooser = new DirectoryChooser();

            //zeigt den "save" Fenster

            fileSave = dirChooser.showDialog(new Stage());
            //solange fenster offen
            if (fileSave != null) {
                //speichere den Ordner ab
                this.choosenPath = fileSave.getAbsolutePath();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //attached to save path as well
    public void openSavepathExplorer() {
        try {
            Runtime.getRuntime().exec("explorer.exe " + this.choosenPath);
        } catch (IOException i) {
            i.printStackTrace();
        }

    }

    public void replaceTitleWith(String defaultTitle, String titleFromView) {
        File oldF, newF;
        try {
            oldF = new File(this.choosenPath + "/" + defaultTitle + ".mp3");
            newF = new File(this.choosenPath + "/" + titleFromView + ".mp3");
            if (oldF.exists()) {
                oldF.renameTo(newF);
            } else {
                throw new Exception("Could not change name!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check if it has Updates. if yes, the if condition in Controller will be active.
    public boolean hasUpdate() throws IOException, ExecutionException, InterruptedException {
       //TODO Zeile 155 funktioniert nicht. Man kann komischerweise nicht aus dem Server lesen.Eventuell mal mit Raspberry PI probieren ob er int readet!
        try {
            //rudralovesparo.ddns.net
            client = checkAndReturnSocket(new Socket(),3121);
            //client.connect(new InetSocketAddress("89.163.163.162", 3121), 3000);

            if (client.isConnected()) {
                dosFromServer = new DataOutputStream(client.getOutputStream());
                disFromServer = new DataInputStream(client.getInputStream());
                serverID = disFromServer.readInt();
                System.out.println("CURRENT ID:" + this.ID);
                System.out.println("RECEIVING ID:" + serverID);
                if (serverID > this.ID) {

                    currentPath = Paths.get("");
                    currentLocation = currentPath.toAbsolutePath().toString() + "\\DonutDownloaderV1." + this.serverID + ".jar";
                    dosFromLocal = new DataOutputStream(new FileOutputStream(currentLocation));
                    dosFromServer.writeBoolean(true);
                    dosFromServer.flush();
                    return true;
                } else {
                    dosFromServer.writeBoolean(false);
                    dosFromServer.flush();
                    return false;
                }

            } else if (client.isInputShutdown() || client.isOutputShutdown() || client.isClosed()) {
                dosFromServer.writeBoolean(false);
                dosFromServer.flush();
                return false;
            } else return false;
        } catch (SocketTimeoutException s) {
            System.out.println("CONNECTION TIMEOUT!");
            return false;

        } catch (IOException i) {
            System.out.println("NO CONNECTION FOUND!");
            // i.printStackTrace();
            return false;
        }

    }

    //retrieve lastVersion and download it.
    public void processUpdate() {
        new UpdateClass(this.disFromServer, this.dosFromLocal,currentLocation).start();
    }

    public void showWhatsNew(View view) {
        view.whatsNewDialog = new Alert(Alert.AlertType.INFORMATION);
        view.whatsNewDialog.setHeaderText("Whats New in Version V1." + this.ID + " ?");
        view.whatsNewDialog.setContentText(getWhatsReallyNew());
        view.whatsNewStage = (Stage) view.whatsNewDialog.getDialogPane().getScene().getWindow();
        view.whatsNewStage.setAlwaysOnTop(true);
        view.whatsNewStage.show();
    }

    public String getWhatsReallyNew() {
        return "#~Whats new? created\n" +
                "#~GUI was improved\n" +
                "#~Reduced DonutDownloader from 22MB to approx. 11MB\n" +
                "#~Pattern Optimization\n" +
                "#~Special Characters removed\n" +
                "#~Bugs fixed\n" +
                "#~Login System created. Visit the Developer for your Account Details!" +
                "\n\n\n\nDonutDownloader is ONLY for personal purposes!\nAll rights reserved.";

    }

    public void deletePreviousVersions() {
        //delete previous versions
        try {

            File filePaths = new File(Paths.get("").toAbsolutePath().toString());

            File[] files = filePaths.listFiles();

            for (File data : files) {
                for (int i = 0; i < this.ID; i++) {
                    String[] oldVersionsDictionary = {"donutdownloader.jar", "DonutDownloader.jar", "DonutDownloaderV1." + i + ".jar", "DonutDownloaderV1.java", "donutdownloader(" + i + ").jar"};
                    for (int j = 0; j < oldVersionsDictionary.length; j++) {
                        try {
                            if (data.isFile() && (data.getName().contains(oldVersionsDictionary[j]))) {
                                System.out.println(data.getName());
                                boolean deleted = data.delete();
                                break;
                            }

                        } catch (ArrayIndexOutOfBoundsException s) {
                            //s.printStackTrace();

                            continue;

                        }
                    }
                }
            }
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    public boolean checkLogin(String login, String password) {

        DataInputStream dis = null;
        DataOutputStream dos = null;
        Socket client = new Socket();
        try {
            client = checkAndReturnSocket(client,3122);
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());

            dos.writeUTF(login);
            dos.flush();

            dos.writeUTF(password);
            dos.flush();

            boolean canLog = dis.readBoolean();
            return canLog;

        } catch (IOException i) {
            i.printStackTrace();
            return false;
            //Platform.exit();

        } finally {
            try {
                if (dis != null)
                    dis.close();
                if (dos != null)
                    dos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnectedToLoginServer() {
        Socket client = checkAndReturnSocket(new Socket(),3122);
          return client.isConnected();
    }
    private Socket checkAndReturnSocket(Socket client, int port){

        int index = 0;
        while(index <this.connectionList.length){
            try {
                client.connect(new InetSocketAddress(this.connectionList[index], port), 500);
                return client;
            }catch (IOException i){
                index++;
                client = new Socket();
                continue;
            }
        }

        if(client.isConnected()){
            return client;
        }

        return client;
    }
}

class innerProcessClass extends Thread{
        private ObservableList<String> urllist;
        private ListView<String> convertList,downloadList;
        private String choosenPath;


    public innerProcessClass(String path,ObservableList<String> urllist,ListView<String> downloadList,ListView<String> convertList){
        this.urllist = urllist;
        this.convertList = convertList;
        this.downloadList = downloadList;
        this.choosenPath = path;
    }
    @Override
    public void run(){
       ObservableList<CompletableFuture<String>> temp = FXCollections.observableArrayList();

       for(String youtubeUrl: this.urllist){

            System.out.println("Thread gestartet!");
            temp.add(CompletableFuture.supplyAsync(new MP3(youtubeUrl,this.choosenPath)));

           //musste so hässlig sein, weil er irgendwas mit not fx thread labert...
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   for (int j = 0; j < temp.size(); j++) {
                       try {
                           downloadList.getItems().remove(j);
                           downloadList.getItems().add(""+temp.get(j).get());
                           temp.remove(j);
                       }catch(Exception e) {
                           e.printStackTrace();
                       }
                   }
               }
           });
        }
    }
}
class UpdateClass extends Thread{

    private DataOutputStream dosFromLocal;
    private DataInputStream disFromServer;
    private String choosenPath;
    public UpdateClass(DataInputStream disServer,DataOutputStream dosLocal,String choosenPath){
        this.disFromServer= disServer;
        this.dosFromLocal = dosLocal;
        this.choosenPath = choosenPath;
    }

    @Override
    public void run(){
        System.out.println("PROCESS UPDATE");
        try{
            // Get length of the FIle
            long fileSizeInBytes =  disFromServer.readLong();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            System.out.println("RECEIVING "+fileSizeInMB+" MB DATA FROM SERVER");

            byte[] buffer = new byte[(int)fileSizeInBytes+8192];
            int temp;

            while((temp = disFromServer.read(buffer)) != -1){
                dosFromLocal.write(buffer,0,temp);
            }
            System.out.println("DONE!\n\n");
            //Start new downloaded program
            Desktop.getDesktop().open(new File(this.choosenPath));
            //Exit the Current program and kill every running Thread.
            final ExecutorService exec = Executors.newCachedThreadPool();
            exec.shutdown();
            System.out.println("BACKGROUND TASKS WERE EXECUTED");

            Platform.exit();

        }catch (Exception i){
            System.out.println("PROCESS UPDATE FAILED");
            i.printStackTrace();
        }finally {

            try{
                if(dosFromLocal != null)
                    dosFromLocal.close();
                if(dosFromLocal!= null)
                    dosFromLocal.close();

                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}