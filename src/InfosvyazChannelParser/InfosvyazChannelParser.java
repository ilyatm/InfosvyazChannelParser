package InfosvyazChannelParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by temnikov.ia on 11.01.17.
 *
 * InfoSvyaz(FocusLife) Microimpuls Middleware API - http://mw.ats.99.ru/ or internal http://10.0.0.4/
 *
 * api_key, sess_key and auth_key may be wrong, need to tcpdump it.
 *
 * Get channel list
 *   http://10.0.0.4:8002/api/tvmiddleware/api/channel/list/?
 *     timezone=12&
 *     timeshift=0&
 *     device=mag&
 *     client_id=1&
 *     api_key=BymiRMhicdjeI9dMMJy3Y96t1bFatWA22LGCOmTDO5cElpwD6Wpr1YAEwI9nihT6&
 *     sess_key=139146&
 *     authkey=345c3c8185894a460425f5e56d54ce582562aab0aec3fddc3e2e618eaeb347d965cbabb4022e5e0cf459849366c5aff41a44ae384ddad9d7e0b6e9b90faec2a2
 *
 * Get categories list
 *   http://10.0.0.4:8002/api/tvmiddleware/api/category/list/?
 *     device=mag&
 *     client_id=1&
 *     api_key=BymiRMhicdjeI9dMMJy3Y96t1bFatWA22LGCOmTDO5cElpwD6Wpr1YAEwI9nihT6&
 *     sess_key=139146&
 *     authkey=345c3c8185894a460425f5e56d54ce582562aab0aec3fddc3e2e618eaeb347d965cbabb4022e5e0cf459849366c5aff41a44ae384ddad9d7e0b6e9b90faec2a2
 */
public class InfosvyazChannelParser {

    private final static String pathCategoriesXML = "/tmp/cat_list2.xml";
    private final static String pathChannelsXML = "/tmp/ch_list2.xml";
    private final static String pathM3U = "/tmp/infosvyaz_20170111.m3u";

    private static HashMap<Integer, String> Categories = new HashMap<>();

    private static class Channel {
        int id;
        String name;
        String url;
        String icon;
        String category;

        private Channel(Element xmlChannel) {
            this.id = Integer.parseInt(xmlChannel.getElementsByTagName("id").item(0).getTextContent());
            this.name = xmlChannel.getElementsByTagName("name").item(0).getTextContent();
            this.url = xmlChannel.getElementsByTagName("url").item(0).getTextContent();
            this.icon = xmlChannel.getElementsByTagName("icon").item(0).getTextContent();
            this.category = Categories.get(Integer.parseInt(xmlChannel.getElementsByTagName("category_id").item(0).getTextContent()));
        }

        public String toString() {
            return String.format("#EXTINF:-1 tvg-name=\"%s\" group-title=\"%s\" tvg-logo=\"%s\", %s", name, category, icon, name);
        }

        private String getUrl() {
            return url;
        }
    }

    private static void setCategories(NodeList nl) {
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                Element el = (Element) n;
                int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
                String name = el.getElementsByTagName("name").item(0).getTextContent();
                Categories.put(id, name);
            }
        }
    }

    private static NodeList getNodes(String FilePath, String Tag) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(FilePath);
            doc.getDocumentElement().normalize();
            return doc.getElementsByTagName(Tag).item(0).getChildNodes();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        NodeList nList = getNodes(pathCategoriesXML, "categories");
        setCategories(nList);
        NodeList nList2 = getNodes(pathChannelsXML, "channels");
        try {
            PrintWriter writer = new PrintWriter(pathM3U, "UTF-8");
            writer.println("#EXTM3U");
            if (nList2 != null) {
                for (int i = 0; i < nList2.getLength(); i++) {
                    Node n = nList2.item(i);
                    Element el = (Element) n;
                    Channel c1 = new Channel(el);
                    System.out.println(c1);
                    System.out.println(c1.getUrl());
                    writer.println(c1);
                    writer.println(c1.getUrl());
                }
                writer.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
