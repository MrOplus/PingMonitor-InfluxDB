package com.github.kooroshh;

import javafx.util.Pair;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CliParser {
    public static final String CONFIG = "config";
    public static HashMap<String,String> parse(String[] args){
        HashMap<String,String> output = new HashMap<>();
        Option configFile = Option.builder("c")
                .required(true)
                .desc("path to config file")
                .longOpt("config")
                .hasArg()
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        options.addOption(configFile);
        CommandLine commandLine;
        try
        {
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("c"))
            {
                String filePath = commandLine.getOptionValue("c");
                output.put(CliParser.CONFIG,filePath);
            }
        }
        catch (ParseException exception)
        {
            System.out.print("Parse error: ");
            System.out.println(exception.getMessage());
            System.exit(-1);
        }
        return output;
    }
}
