package MP3;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import interfaces.Downloadable;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Playlist implements Supplier<String>, Downloadable {
    private String urlYoutube,path,name,urlPlaylist;
    private WebClient webClient;
    private HtmlPage youtube;

    public  Playlist(String urlYoutube,String path){
        this.urlYoutube = urlYoutube;
        this.path = path;
        this.urlYoutube = urlYoutube;
        ignoreLogs();

        try {

            createWebsite();
            processYoutubeLink();
            downloadMp3FromServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //create Websiteframe
    @Override
    public synchronized void createWebsite() throws Exception {
        try {
            //Youtube Playlist link
            webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setJavaScriptEnabled(true);
            youtube = webClient.getPage(this.urlPlaylist);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public synchronized void processYoutubeLink() throws IOException, InterruptedException {
        try {
            this.name = null;
            final HtmlForm form = youtube.getFirstByXPath("//form[@action='index.php?p=convert']");
            final HtmlTextInput urlField = form.getFirstByXPath("//input[@name='url']");
            final HtmlButton convertButton = form.getFirstByXPath("//button[@type='submit']");
            System.out.println("Accessing Convert2Mp3...");
            urlField.setText(this.urlYoutube);
            System.out.println("Converting...");
            System.out.println("Preparing mp3 file for download...");
            converterPage = convertButton.click();

            windowTitle();

            final HtmlAnchor continueButton = converterPage.getFirstByXPath("//a[@class='btn']");
            converterPage = continueButton.click();

            windowTitle();
            System.out.println("determine title");

            determineTitle();
        }catch(Exception e){
            //Lied ist nicht verfügbar!
            this.name = "NotAvailableInYourCountry";
            e.printStackTrace();
        }
    }
    @Override
    public synchronized void downloadMp3FromServer() throws IOException {

        HtmlAnchor downloadAnchor = converterPage.getFirstByXPath("//a[@class='btn btn-success btn-large']");
        InputStream reader = null;
        OutputStream os = null;

        try {
            reader = downloadAnchor.click().getWebResponse().getContentAsStream();
            os = new FileOutputStream(this.path+"\\"+this.name+".mp3");
            System.out.println("Writing to: "+this.path+"\\"+this.name+".mp3");
            byte[] buffer = new byte[8192];
            int read;
            while((read = reader.read(buffer)) != -1){
                os.write(buffer,0,read);
            }
            System.out.println("Download done! Please check your path.");


        }catch (Exception i){
            i.printStackTrace();
        }finally{
            try{
                if(reader != null)
                    reader.close();

                if(os != null)
                    os.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public synchronized String get(){
        return getTitle();
    }
    public synchronized String getTitle(){

        return this.name;
    }
    @Override
    public void ignoreLogs() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

    }
    @Override
    public void windowTitle() {
        System.out.println("Window Url: " + this.converterPage.getUrl());
    }
    @Override
    public synchronized void determineTitle(){
        try {

            final HtmlTextInput inputfield = amnesty.getFirstByXPath("//input[@title='Enter YouTube URL']");
            final HtmlInput input = amnesty.getFirstByXPath("//input[@value='Go']");
            inputfield.setText(this.urlYoutube);
            amnesty = input.click();
            webClient2.waitForBackgroundJavaScript(2000);
            HtmlAnchor title = amnesty.getAnchorByHref(this.urlYoutube);
            this.name=title.getTextContent();
            this.name = this.name.replaceAll("\\W+","");
            System.out.println(name);
        }catch(Exception e){
            this.name = "NameNotParsed"+ new Random().nextInt(99999);
            e.printStackTrace();
        }finally{
            try{
                if(webClient2 != null)
                    webClient2.close();
                if(amnesty != null)
                    amnesty = null;
            }catch (Exception e){

            }
        }
    }
}
