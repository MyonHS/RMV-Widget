import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class API_Handler {

    //enum to specify request Type
    enum requestType{
        DEPARTUREBOARD,
        ARRIVALBOARD
    }
    private static Vector<Stop> stopVector = new Vector<>();
    private static FileReader fileReader;
    private static BufferedReader bufferedReader;
    private static String apiKey;

    public static void init()
    {
        //read and set api key
        try {
            fileReader = new FileReader("apikey.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        bufferedReader = new BufferedReader(fileReader);
        try {
            apiKey = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(apiKey==null)
        {
            JOptionPane.showMessageDialog(null, "API Key not set!", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.close();
        }

    }

    public static void parseStops()
    {
        FileInputStream stopFile;

        try
        {
            stopFile = new FileInputStream("Haltestellen.xlsx");
        } catch (FileNotFoundException e)
        {
                throw new RuntimeException(e);
        }

        Workbook workbook;

        try {
            workbook = new XSSFWorkbook(stopFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet = workbook.getSheetAt(0);

        for(Row currentRow : sheet)
        {
            if(currentRow.getRowNum()==0) continue;

            String stopName = currentRow.getCell(4).getRichStringCellValue().getString();
            int id = (int)currentRow.getCell(0).getNumericCellValue();
            Stop newStop = new Stop(stopName, id);
            stopVector.add(newStop);
        }

        try {
            stopFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getStopNameArray()
    {
        String[] result = new String[stopVector.size()];

        int index=0;
        for(Stop currentStop : stopVector)
        {
            result[index]=currentStop.stopName;
            index++;
        }

        return result;
    }

    private static JSONObject sendAPIRequest(int startStopId, int endStopId, requestType type, int duration)
    {
        JSONObject result;
        URL url=null;
        HttpURLConnection con;

        switch (type)
        {
            case DEPARTUREBOARD :
                try {
                    url = new URL("https://www.rmv.de/hapi/departureBoard?accessId="+apiKey+"&id="+startStopId);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case ARRIVALBOARD:
                try {
                    url = new URL("https://www.rmv.de/hapi/arrivalBoard?accessId="+apiKey+"&id="+startStopId);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        try {

            assert url != null;
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.getResponseCode();

            //read response (as xml)
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            //convert xml to json
            String xlmResponse = content.toString();
            result = XML.toJSONObject(xlmResponse);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static Vector<String> getDeparturesByStopname(String stopName, int duration)
    {
        Vector<String> result = new Vector<>();

        int stopID = getStopIdByName(stopName);

        JSONObject response = sendAPIRequest(stopID,-1,requestType.DEPARTUREBOARD,duration);

        if(!response.toString(4).contains("stop")) //check if departures exist
        {
            result.add("Keine Abfahrten in der nächsten Stunde");
            return result;
        }

        int departuresNumber = response.getJSONObject("DepartureBoard").getJSONArray("Departure").length();

        for(int currentDepartureIndex=0; currentDepartureIndex<departuresNumber; currentDepartureIndex++)
        {
            JSONObject currentDepartureObject = response.getJSONObject("DepartureBoard").getJSONArray("Departure").getJSONObject(currentDepartureIndex);
            String newDeparture="";

            System.out.println(currentDepartureIndex);

            //since they "updated" to the new api sometimes the name and direction information is missing randomly.
            //I think it's a bug on their site
            try{
                newDeparture+=currentDepartureObject.get("name");
            }
            catch(JSONException jsonexc)
            {
                //discard current departure. Not much we can do
                continue;
            }

            if(currentDepartureObject.toString().contains("rtTrack")) //We only get track information for busses and Trains
            {
                newDeparture+=" Track: "+currentDepartureObject.get("rtTrack");
            }
            newDeparture+="  "+currentDepartureObject.get("time");

            newDeparture+="  "+currentDepartureObject.get("direction");

            result.add(newDeparture);
        }

        return result;
    }

    public static Vector<String> getArrivalsByStopname(String stopName, int duration)
    {
        Vector<String> result = new Vector<>();

        int stopID = getStopIdByName(stopName);

        JSONObject response = sendAPIRequest(stopID,-1,requestType.ARRIVALBOARD, duration);

        if(!response.toString(4).contains("stop")) //check if Arrivals exist
        {
            result.add("Keine Ankunften in der nächsten Stunde");
            return result;
        }

        int arrivalsNumber = response.getJSONObject("ArrivalBoard").getJSONArray("Arrival").length();

        for(int currentArrivalIndex = 0; currentArrivalIndex<arrivalsNumber; currentArrivalIndex++)
        {
            JSONObject currentArrivalObject = response.getJSONObject("ArrivalBoard").getJSONArray("Arrival").getJSONObject(currentArrivalIndex);
            String newArrival="";

            newArrival+=currentArrivalObject.get("name");

            if(currentArrivalObject.toString().contains("rtTrack")) //We only get track information for busses and Trains
            {
                newArrival+=" Track: "+currentArrivalObject.get("rtTrack");
            }

            newArrival+="  "+currentArrivalObject.get("time");
            newArrival+="  "+currentArrivalObject.get("origin");

            result.add(newArrival);
        }

        return result;
    }

    public static int getStopIdByName(String stopName)
    {
        int result=-1;

        for(Stop currentStop : stopVector)
        {
            if(currentStop.stopName.equals(stopName)) return currentStop.id;
        }

        return result;
    }
}
