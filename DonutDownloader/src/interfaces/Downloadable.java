package interfaces;

import java.io.IOException;

public interface Downloadable {
    void createWebsite() throws Exception;
    void processYoutubeLink() throws IOException, InterruptedException;
    void downloadMp3FromServer() throws IOException;
    void ignoreLogs();
    void windowTitle();
    void determineTitle();
}
