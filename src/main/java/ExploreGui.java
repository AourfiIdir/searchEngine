import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ExploreGui extends JFrame {
    String url;
    JButton allHtml;
    JButton loadH1;
    JButton loadH2;
    JButton loadP;
    JPanel mainPanel;
    public ExploreGui(String url){
        this.url = url;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("explore");
        setSize(500,500);
        initializer();
        initializeListeners();
        setContentPane(mainPanel);
        setVisible(true);
    }
    public void loadHtml(String url,String source) throws IOException {
        Document doc = Jsoup.connect(url).get();
        switch(source){
            case "loadHtml":
                String HtmlPage = doc.outerHtml();
                System.out.println(HtmlPage);
                break;
            case "loadH1":
                Elements H1 = doc.select("h1");
                for(Element h1 :H1){
                    System.out.println(h1.text());
                }
                break;
            case "loadH2":
                Elements H2 = doc.select("h2");
                for(Element h2 :H2){
                    System.out.println(h2.text());
                }
                break;
            case "loadP":
                Elements P = doc.select("p");
                for(Element p :P){
                    System.out.println(p.text());
                }
                break;
            default:
                break;
        }


    }

    public void initializer(){
        mainPanel = new JPanel();
        allHtml = new JButton("loadHtml");
        loadH1 = new JButton("loadH1");
        loadH2 = new JButton("loadH2");
        loadP = new JButton("loadP");
        mainPanel.add(allHtml);
        mainPanel.add(loadH1);
        mainPanel.add(loadH2);
        mainPanel.add(loadP);

    }
    void initializeListeners(){
        ActionListener buttonListen = e->{
            JButton sourceBtn = (JButton)e.getSource();
            String source = sourceBtn.getText();
            try {
                loadHtml(url,source);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
        allHtml.addActionListener(buttonListen);
        loadH1.addActionListener(buttonListen);
        loadH2.addActionListener(buttonListen);
        loadP.addActionListener(buttonListen);

    }

}
