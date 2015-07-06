package com.ecml;

 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
 
public class ReadWriteXMLFile {

	static boolean begin = false;

    /** Create the XML file on the internal storage of the application
     *
     * @param context
     */
    public static void create(Context context) {
        try {
            context.deleteFile("SequenceOfActivity.xml");
            FileOutputStream fos = context.openFileOutput("SequenceOfActivity.xml", Context.MODE_PRIVATE);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag("", "sequenceOfactivity");
            serializer.endTag("", "sequenceOfactivity");
            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/** Read an XML file if it exists and return the list of activities in ActivityParameter objects
	 *
	 * @param context
	 * @return
	 */
	public static ArrayList<ActivityParameters> read(Context context){
    	ArrayList<ActivityParameters> list = new ArrayList<ActivityParameters>();
		if (!begin) {
			create(context);
			begin = !begin;
		}
		else {
			try {
				FileInputStream f = context.openFileInput("SequenceOfActivity.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);

				NodeList nList = doc.getElementsByTagName("activity");

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) nNode;
                        int number = Integer.parseInt(element.getAttribute("number"));
                        String type = element.getAttribute("type");
                        int duration = Integer.parseInt(element.getElementsByTagName("duration").item(0).getTextContent());
                        String song = element.getElementsByTagName("song").item(0).getTextContent();
                        int tempo = Integer.parseInt(element.getElementsByTagName("tempo").item(0).getTextContent());
                        int level = Integer.parseInt(element.getElementsByTagName("gameLevel").item(0).getTextContent());
                        boolean active = Boolean.parseBoolean(element.getElementsByTagName("active").item(0).getTextContent());
                        boolean finished = Boolean.parseBoolean(element.getElementsByTagName("finished").item(0).getTextContent());
                        int countdown = Integer.parseInt(element.getElementsByTagName("countdown").item(0).getTextContent());
                        list.add(new ActivityParameters(number, type, duration, song, tempo, active, finished, countdown));
					}
				}
                f.close();
			}
            catch (Exception e) {
				if (e instanceof FileNotFoundException) {
					Log.d("ReadWriteXMLFile", "File not found");
				} else {
					e.printStackTrace();
				}
			}
		}
		return list;
    }

	/** Write on an existing XML file an activity
	 *
	 * @param parameters
	 * @param context
	 */
	public static void write(ActivityParameters parameters, Context context) {
		try {
			ArrayList<ActivityParameters> list = read(context);
			list.add(parameters);

			FileOutputStream fos = context.openFileOutput("SequenceOfActivity.xml",Context.MODE_PRIVATE);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			serializer.startTag("", "sequenceOfactivity");
			for (ActivityParameters a : list) {
				//Element activity
				serializer.startTag("", "activity");
				serializer.attribute("", "type", a.getActivityType());
				serializer.attribute("", "number", String.valueOf(a.getNumber()));

				//Element duration
				serializer.startTag("", "duration");
				serializer.text(String.valueOf(a.getDuration()));
				serializer.endTag("", "duration");

				//Element song
				serializer.startTag("", "song");
				serializer.text("song");
				serializer.endTag("", "song");

				//Element tempo
				serializer.startTag("", "tempo");
				serializer.text(String.valueOf(a.getTempo()));
				serializer.endTag("", "tempo");

				//Element gameLevel
				serializer.startTag("", "gameLevel");
				serializer.text(String.valueOf(a.getGameLevel()));
				serializer.endTag("", "gameLevel");

				//Element active
				serializer.startTag("", "active");
				serializer.text(String.valueOf(a.isActive()));
				serializer.endTag("", "active");

				//Element finished
				serializer.startTag("", "finished");
				serializer.text(String.valueOf(a.isFinished()));
				serializer.endTag("", "finished");

				//Element countdown
				serializer.startTag("", "countdown");
				serializer.text(String.valueOf(a.getCountdown()));
				serializer.endTag("", "countdown");

				serializer.endTag("", "activity");
			}
			serializer.endTag("","sequenceOfactivity");

			serializer.endDocument();
			serializer.flush();
			fos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Edit one of the activity of the XML file
	 *
	 * @param parameters
	 * @param context
	 */
	public static void edit(ActivityParameters parameters, Context context) {
		try {
			FileInputStream f = context.openFileInput("SequenceOfActivity.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);

			NodeList nList = doc.getElementsByTagName("activity");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nNode;
					int number = Integer.parseInt(element.getAttribute("number"));
					if (number == parameters.getNumber()) {
						Log.d("ReadWriteXMLFile:Edit", "number = " + String.valueOf(number));
						element.setAttribute("type", parameters.getActivityType());
						element.getElementsByTagName("duration").item(0).setTextContent(String.valueOf(parameters.getDuration()));
						element.getElementsByTagName("song").item(0).setTextContent(parameters.getSong());
						element.getElementsByTagName("tempo").item(0).setTextContent(String.valueOf(parameters.getTempo()));
						element.getElementsByTagName("gameLevel").item(0).setTextContent(String.valueOf(parameters.getGameLevel()));
						element.getElementsByTagName("active").item(0).setTextContent(String.valueOf(parameters.isActive()));
						element.getElementsByTagName("finished").item(0).setTextContent(String.valueOf(parameters.isFinished()));
						element.getElementsByTagName("countdown").item(0).setTextContent(String.valueOf(parameters.getCountdown()));
						Log.d("ReadWriteXMLFile:Edit", "countdown = " + element.getElementsByTagName("countdown").item(0).getTextContent());
					}
				}
			}
            f.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

    /** Read on an XML file and return the ActivityParameters given by its number
     *
     * @param num
     * @return
     */
    public static ActivityParameters readActivityByNumber(int num, Context context) {
        try {
            FileInputStream f = context.openFileInput("SequenceOfActivity.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            NodeList nList = doc.getElementsByTagName("activity");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    int number = Integer.parseInt(element.getAttribute("number"));
                    if (number == num) {
                        String type = element.getAttribute("type");
                        int duration = Integer.parseInt(element.getElementsByTagName("duration").item(0).getTextContent());
                        String song = element.getElementsByTagName("song").item(0).getTextContent();
                        int tempo = Integer.parseInt(element.getElementsByTagName("tempo").item(0).getTextContent());
                        int level = Integer.parseInt(element.getElementsByTagName("gameLevel").item(0).getTextContent());
                        boolean active = Boolean.parseBoolean(element.getElementsByTagName("active").item(0).getTextContent());
                        boolean finished = Boolean.parseBoolean(element.getElementsByTagName("finished").item(0).getTextContent());
                        int countdown = Integer.parseInt(element.getElementsByTagName("countdown").item(0).getTextContent());
                        Log.d("ReadWriteXMLFile:ReadAc", "countdown = " + String.valueOf(countdown));
                        return new ActivityParameters(number, type, duration, song, tempo, active, finished, countdown);
                    }
                }
            }
            f.close();
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
  
  }

