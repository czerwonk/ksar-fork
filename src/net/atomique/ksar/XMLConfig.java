/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author alex
 */
public class XMLConfig extends DefaultHandler {

    public XMLConfig(String filename, boolean replace) {
        boolean added = false;
        load_document(filename);
        if (replace) {
            kSarConfig.shortcut_window_list.clear();
            kSarConfig.association_list.clear();
            kSarConfig.startup_windows_list.clear();
            kSarConfig.shortcut_window_processlist.clear();
        }
        Iterator it = shortcutlist.iterator();
        while (it.hasNext()) {
            XMLShortcutItem tmp = (XMLShortcutItem) it.next();
            added = true;
            kSarConfig.shortcut_window_list.put(tmp.toString(), tmp.getDescription());
            if (tmp.getStartup()) {
                kSarConfig.startup_windows_list.add(tmp.getDescription());
            }
            if ( tmp.hasprocesslist() ) {
                kSarConfig.shortcut_window_processlist.put(tmp.toString(), tmp.getProcesslistCommand());
            }
        }
        if (added) {
            kSarConfig.writeDefault();
        }
    }

    private void load_document(String xmlfile) {
        SAXParserFactory fabric = null;
        SAXParser parser = null;
        try {
            fabric = SAXParserFactory.newInstance();
            parser = fabric.newSAXParser();
            parser.parse(xmlfile, this);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempval = new String(ch, start, length);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("ksar-config".equals(qName)) {
            // config found 
            shortcutlist = new ArrayList<XMLShortcutItem>();
            associationlist = new ArrayList<XMLAssociationItem>();
        }
        if ("shortcut".equals(qName)) {
            cur_shortcut = new XMLShortcutItem();
            inshortcut = true;
        }
        if ("association".equals(qName)) {
            cur_association = new XMLAssociationItem();
            inassociation = true;
        }

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("ksar-config".equals(qName)) {
            // end of config make tag
            beenparse = true;
        }
        if ("shortcut".equals(qName)) {
            if (cur_shortcut.valid()) {
                shortcutlist.add(cur_shortcut);
            } else {
                System.err.println("Err: " + cur_shortcut.getErrormsg());
                cur_shortcut = null;
            }
            inshortcut = false;
        }
        if ("association".equals(qName)) {
            if (cur_association.valid()) {
                associationlist.add(cur_association);
            } else {
                System.err.println("Err: " + cur_association.getErrormsg());
                cur_association = null;
            }
            inassociation = false;
        }

        if (inshortcut) {
            if ("description".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setDescription(tempval);
            }
            if ("type".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setType(tempval);
            }
            if ("host".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setHost(tempval);
            }
            if ("login".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setLogin(tempval);
            }
            if ("filename".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setFilename(tempval);
            }
            if ("command".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setCommand(tempval);
            }
            if ("startup".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setStartup(tempval);
            }
            if ("processlist".equals(qName) && cur_shortcut != null) {
                cur_shortcut.setProcesslist(tempval);
            }
        }
        if (inassociation) {
            if ("description".equals(qName) && cur_association != null) {
                cur_association.setDescription(tempval);
            }
            if ("command".equals(qName) && cur_association != null) {
                cur_association.setCommand(tempval);
            }
            if ("processlist".equals(qName) && cur_association != null) {
                cur_association.setProcesslist(tempval);
            }
        }
    }
    public boolean beenparse = false;
    public ArrayList<XMLShortcutItem> shortcutlist = null;
    public ArrayList<XMLAssociationItem> associationlist = null;
    private XMLShortcutItem cur_shortcut = null;
    private XMLAssociationItem cur_association = null;
    private String tempval;
    private boolean inshortcut = false;
    private boolean inassociation = false;
}
