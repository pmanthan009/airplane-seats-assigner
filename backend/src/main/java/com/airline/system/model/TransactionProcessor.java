/**
 * TransactionProcessor
 *
 * PURPOSE: Processes transaction files (RESERVE/CANCEL operations) and applies them to the Airline System.
 */

package com.airline.system.model;
import java.io.*;

public class TransactionProcessor {
    private AirlineSystem system;

    /**
     * TransactionProcessor constructor
     * param: sys (AirlineSystem)
     * initializes the processor with a reference to the airline system where flights are stored
     * return type: void
     **/
    public TransactionProcessor(AirlineSystem sys) {
        this.system = sys;
    }

    /**
     * processFile method
     * param: filename (String)
     * reads and processes transactions from the given file, applying RESERVE and CANCEL operations
     * return type: void
     **/
    public void processFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        boolean eof = false;

        while (!eof) {
            line = br.readLine();
            if (line == null) {
                eof = true;
            } else {
                line = line.trim();
                boolean skipLine = false;
                if (line.length() == 0) {
                    skipLine = true;
                }

                if (!skipLine) {
                    String[] headerParts = line.split("\\s+");
                    String type = headerParts[0];
                    int flightNo = parseKeyInt(headerParts, "FLIGHT:");
                    Integer sectionIdx = parseKeyIntOpt(headerParts, "SECTION:");
                    int count = parseKeyInt(headerParts, "COUNT:");
                    Passenger[] group = new Passenger[count];

                    int i = 0;
                    boolean groupEOF = false;
                    while (i < count && !groupEOF) {
                        String pline = br.readLine();
                        if (pline == null) {
                            groupEOF = true;
                        } else {
                            pline = pline.trim();
                            Passenger p = parsePassengerLine(pline, type.equals("RESERVE"));
                            group[i] = p;
                            i++;
                        }
                    }

                    Flight f = system.findFlight(flightNo);
                    if (f == null) {
                        System.out.println("ERROR: flight " + flightNo + " not found");
                    } else {
                        if (type.equals("RESERVE")) {
                            if (sectionIdx == null) {
                                System.out.println("ERROR: RESERVE without SECTION not allowed");
                            } else {
                                f.reserve(sectionIdx, group);
                            }
                        } else if (type.equals("CANCEL")) {
                            int s = -1;
                            if (sectionIdx != null) {
                                s = sectionIdx;
                            }
                            f.cancel(s, group);
                        } else {
                            System.out.println("ERROR: unknown transaction type: " + type);
                        }
                    }
                }
            }
        }
        br.close();
    }

    /**
     * parsePassengerLine method
     * param: line (String), allowPref (boolean)
     * parses a passenger line of the form "Last,First,YYYY/MM/DD[,P]" and returns a Passenger object
     * return type: Passenger
     **/
    private Passenger parsePassengerLine(String line, boolean allowPref) {
        String[] tokens = line.split(",");
        String last = tokens[0].trim();
        String first = tokens[1].trim();
        String dob = tokens[2].trim();
        char preference = '\0';
        if (allowPref && tokens.length >= 4) {
            String p = tokens[3].trim();
            if (p.length() > 0) {
                preference = p.charAt(0);
            }
        }
        return new Passenger(last, first, dob, preference);
    }

    /**
     * parseKeyInt method
     * param: parts (String[]), keyPrefix (String)
     * searches the parts array for an entry starting with keyPrefix and parses the integer value after the prefix
     * return type: int
     **/
    private int parseKeyInt(String[] parts, String keyPrefix) {
        int i;
        for (i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.startsWith(keyPrefix)) {
                String val = p.substring(keyPrefix.length());
                return Integer.parseInt(val);
            }
        }
        throw new RuntimeException("Missing key: " + keyPrefix);
    }

    /**
     * parseKeyIntOpt method
     * param: parts (String[]), keyPrefix (String)
     * searches the parts array for an entry starting with keyPrefix and returns the integer value after the prefix or null if not present
     * return type: Integer
     **/
    private Integer parseKeyIntOpt(String[] parts, String keyPrefix) {
        int i;
        for (i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.startsWith(keyPrefix)) {
                String val = p.substring(keyPrefix.length());
                return Integer.parseInt(val);
            }
        }
        return null;
    }
}