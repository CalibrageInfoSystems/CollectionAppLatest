package com.oilpalm3f.mainapp.GraderFingerprint;

import static com.oilpalm3f.mainapp.cloudhelper.HttpClient.response;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

// ...

public class PostRequestTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String requestDataJsonString = params[1];

        LinkedHashMap<String, String> requestData = convertToHashMap(requestDataJsonString);


        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");


            // Enable writing data to the connection output stream
            connection.setDoOutput(true);

            StringBuilder postData = new StringBuilder();
            for (String key : requestData.keySet()) {
                String value = requestData.get(key);
                postData.append("\"").append(key).append("\":\"").append(value).append("\",");
            }
            postData.deleteCharAt(postData.length() - 1);  // Remove the trailing comma

            // Construct the final POST data string
           // String postDataString = "{" + postData.toString() + "}";

            String postDataString = convertToJsonString(requestData);
            // Log the request object
            Log.d("Request", "URL: " + urlString);
            Log.d("Request", "Method: POST");
            Log.d("Request", "Data: " + postDataString);

            // Create the POST data string
            //String postData = "{\"key1\":\"" + key1Value + "\",\"key2\":\"" + key2Value + "\"}";
           // String postData = "{\"key1\":\"" + key1Value + "\",\"key2\":\"" + key2Value + "\",\"key3\":\"" + key3Value + "\",\"key4\":\"" + key4Value + "\"}";
            //String postData = "{\"key1\":\"" + key1Value + "\",\"key2\":\"" + key2Value + "\",\"key3\":\"" + key3Value + "\",\"key4\":\"" + key4Value + "\"}";

//            Log.d("Request", "URL: " + urlString);
//            Log.d("Request", "Method: POST");
//            Log.d("Request", "Data: " + postDataString);

            // Write the POST data to the connection output stream
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(postDataString);
            outputStream.flush();
            outputStream.close();

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Return the response as a string
                return response.toString();
            } else {
                // Handle the error response
                // ...
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
//            // Handle the API response
//            Log.d("response", response.toString());
//
//            JSONObject jsonResponse = null;
//            try {
//                jsonResponse = new JSONObject(response);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                Log.d("jsonResponse1", jsonResponse.getString("SuccessCode"));
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                Log.d("jsonResponse2", jsonResponse.getString("SuccessMessage"));
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
            // ...
        } else {
            // Handle the error
            // ...Log.d("Nullresult", "ResultIsnull");
        }
    }

    public static String convertToJsonString(HashMap<String, String> requestData) {
        JSONObject jsonObject = new JSONObject(requestData);
        return jsonObject.toString();
    }

//    private HashMap<String, String> convertToHashMap(String jsonString) {
//        HashMap<String, String> hashMap = new HashMap<>();
//        try {
//            JSONObject jsonObject = new JSONObject(jsonString);
//            Iterator<String> keys = jsonObject.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                String value = jsonObject.getString(key);
//                hashMap.put(key, value);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return hashMap;
//    }
//

    private LinkedHashMap<String, String> convertToHashMap(String jsonString) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                hashMap.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }


}

