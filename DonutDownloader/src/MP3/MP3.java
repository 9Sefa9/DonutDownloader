package MP3;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import java.util.Random;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP3 implements Supplier<String> {
    private String urlYoutube,path,name;
    private WebClient webClient,webClient2;
    private HtmlPage converterPage,amnesty;

    public  MP3(String urlYoutube,String path){
        this.urlYoutube = urlYoutube;
        this.path = path;

        //create browser
        ignoreLogs();
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setJavaScriptEnabled(true);

        try {
            createWebsite();
            processYoutubeLink();
            downloadMp3FromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //create Websiteframe
    public synchronized void createWebsite() throws Exception {
        converterPage = webClient.getPage("http://convert2mp3.net/");
        windowTitle();
    }

    public synchronized void processYoutubeLink() throws IOException, InterruptedException {
        try {
            this.name = null;
            final HtmlForm form = converterPage.getFirstByXPath("//form[@action='index.php?p=convert']");
            final HtmlTextInput urlField = form.getFirstByXPath("//input[@name='url']");
            final HtmlButton convertButton = form.getFirstByXPath("//button[@type='submit']");
            System.out.println("Accessing Youtube link...");
            urlField.setText(this.urlYoutube);
            System.out.println("Converting to mp3...");
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

    public void ignoreLogs() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

    }
    public void windowTitle() {
        System.out.println("Window Url: " + this.converterPage.getUrl());
    }
    public synchronized void determineTitle(){
            try {
                webClient2 = new WebClient(BrowserVersion.CHROME);
                webClient2.getOptions().setThrowExceptionOnScriptError(false);
                webClient2.getOptions().setCssEnabled(false);
                webClient2.getOptions().setUseInsecureSSL(true);
                webClient2.getOptions().setJavaScriptEnabled(true);

                amnesty = webClient2.getPage("https://citizenevidence.amnestyusa.org/");
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
