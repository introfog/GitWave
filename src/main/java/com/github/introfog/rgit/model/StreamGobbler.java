package com.github.introfog.rgit.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
    private final InputStream is;
    private final String type;

    private final StringBuilder output;

    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
        output = new StringBuilder();
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String line;
            while ((line = br.readLine()) != null) {
                output.append(type).append("> ").append(line).append("\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String getOutput() {
        return output.toString();
    }
}
