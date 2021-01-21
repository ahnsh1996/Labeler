package com.example.labeler;

public class Server {
    static String url, port;
    Server() {
        url = "xxx.xxx.xxx.xxx";
        port = "8080";
    }
    public static String getBaseUrl() {
        return url+":"+port;
    }
}