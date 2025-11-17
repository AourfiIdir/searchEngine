import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Gui extends JFrame {
    Map<String,String> result;
    JPanel firstPanel;
    Statement insertStatement;
    JPanel mainPanel;
    JLabel search;
    JTextField fieldOfSearch;
    JList<String> list;
    DefaultListModel<String> model;
    JPanel suggestionPanel;
    JScrollPane scroll;
    JButton explore;
    //custom url
    JPanel customized;
    JLabel customizedLabel;
    JTextField customizedInput;
    JButton exploreCustomButton;
    JList<String> suggestionList;
    public static Connection conn;
    public Gui() throws SQLException, IOException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("search engine");
        setSize(500,500);
        initializer();
        setContentPane(mainPanel);
        searchEngine();
        setVisible(true);
    }
    public void initializer(){
        Dimension fieldDimension = new Dimension(200,25);

        mainPanel = new JPanel(new BorderLayout());
        firstPanel = new JPanel();
        model = new DefaultListModel<String>();
        list = new JList<>();
        customized = new JPanel();
        customizedInput = new JTextField();
        customizedInput.setPreferredSize(fieldDimension);
        customizedLabel = new JLabel("enter your customized url");
        suggestionPanel = new JPanel();
        customized.add(customizedLabel);
        customized.add(customizedInput);
        exploreCustomButton = new JButton("explore custom url2é");
        customized.add(exploreCustomButton);
        scroll =  new JScrollPane(list);
        suggestionPanel.add(scroll);
        explore = new JButton("explore");
        listenerInitializer();
        search = new JLabel("search");
        fieldOfSearch = new JTextField();
        fieldOfSearch.setPreferredSize(fieldDimension);

        firstPanel.add(search);
        firstPanel.add(fieldOfSearch);
        firstPanel.add(suggestionPanel);
        mainPanel.add(firstPanel,BorderLayout.NORTH);
        mainPanel.add(customized,BorderLayout.SOUTH);
    }

    public void openExploreWindow(String url) throws IOException {
        new ExploreGui(url);
    }

    public void listenerInitializer(){
        explore.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openExploreWindow(list.getSelectedValue());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        exploreCustomButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    openExploreWindow(customizedInput.getText());
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public Connection createSqlConnection() throws SQLException {
        String url = "jdbc:sqlite:database2.db";
        try {
            conn = DriverManager.getConnection(url);
            insertStatement = conn.createStatement();
            if(conn!=null){
                System.out.println("the connections is established");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return conn;
    }
    public void getTheHtmlPage(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStream in = (InputStream) url.getContent();
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String line=null;
        StringBuffer str = new StringBuffer();
        while((line=bf.readLine())!=null){
            str.append(line);
        }
        String htmlContent = str.toString();
        String fileResult = "page.html";

        try(FileWriter fr = new FileWriter(fileResult,false)){
            fr.write(htmlContent);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void createTable(Connection conn) throws SQLException {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS ipess (" +
                "id INTEGER PRIMARY KEY ,"+
                "ip TEXT , " +
                "title TEXT NOT NULL" +
                ")";

String sqlInsert = "INSERT OR IGNORE INTO ipess (id,ip,title) VALUES " +
                "(1,'93.184.216.34', 'https://example.com'), " +
                "(2,'142.250.190.14', 'https://google.com'), " +
                "(3,'104.16.99.52', 'https://cloudflare.com'), " +
                "(4,'23.63.116.135', 'https://amazon.com'), " +
                "(5,'151.101.1.69', 'https://github.com'), " +
                "(6,'34.102.136.180', 'https://openai.com'), " +
                "(7,'104.244.42.129', 'https://twitter.com'), " +
                "(8,'172.217.16.142', 'https://youtube.com'), " +
                "(9,'13.227.156.76', 'https://aws.amazon.com'), " +
                "(10,'52.216.72.182', 'https://microsoft.com'), " +
                "(11,'185.199.108.153', 'https://github.io'), " +
                "(12,'192.0.78.13', 'https://wordpress.com'), " +
                "(13,'69.63.176.13', 'https://facebook.com'), " +
                "(14,'157.240.22.35', 'https://facebook.net'), " +
                "(15,'23.235.33.133', 'https://digitalocean.com'), " +
                "(16,'127.0.0.1', 'https://localhost'), " +
                "(17,'198.252.206.16', 'https://pypi.org'), " +
                "(18,'151.101.65.121', 'https://npmjs.com'), " +
                "(19,'104.18.21.25', 'https://medium.com'), " +
                "(20,'172.67.211.187', 'https://ubuntu.com');";;

        try (Statement stat = conn.createStatement()) {
            stat.execute(sqlCreate);
            stat.execute(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> getResult(String str,Connection conn) throws SQLException {
        Map<String,String> resultMap= new HashMap<>();

        String request = "SELECT * FROM ipess WHERE title Like '"+str+"%'";
        PreparedStatement prepStat = conn.prepareStatement(request);
        ResultSet result = prepStat.executeQuery();
        while(result.next()){
            String ip = result.getString("ip");
            String title = result.getString("title");
            resultMap.put(title,ip);
        }
        return resultMap;
    }
    public void updatePanel(DefaultListModel<String> model) {
        suggestionPanel.removeAll();
        list.setModel(model);
        suggestionPanel.add(new JScrollPane(list));
        suggestionPanel.add(explore);
        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }
    public void addSelectedLink(){
        
    }
    public void searchEngine() throws SQLException {
        Connection conn = createSqlConnection();
        createTable(conn);
        fieldOfSearch.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                String str = fieldOfSearch.getText();
                try {
                    onTextChange(str);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String str = fieldOfSearch.getText();
                try {
                    onTextChange(str);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String str = fieldOfSearch.getText();
                try {
                    onTextChange(str);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            public void onTextChange(String str) throws SQLException {
                //do a request to get all the titles that starts with str (retrieve also the ip) and put them in a map
                result = getResult(str,conn);
                //put them in  a table

                model.clear();
                for(String i :result.keySet()){
                    model.addElement(i);
                }
                //make the table clickable for any
                updatePanel(model);
                
            }
        });
    }

}
