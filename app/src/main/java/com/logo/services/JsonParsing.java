package com.logo.services;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by puneet on 15/8/17.
 */

public class JsonParsing {

    int TIMEOUT_MILLIS = 20 * 1000;

    // constructor
    public JsonParsing() {}



    public JSONObject httpPost(String url,JSONObject postData,String authToken) {
        String response = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(TIMEOUT_MILLIS);
            con.setRequestProperty("Content-Type", "application/json");

            if (authToken != null) {
                con.setRequestProperty("token", authToken);
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            out.write(postData.toString());
            out.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            response = br.readLine();

            JSONObject jObj = new JSONObject(response.toString());
            return jObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}