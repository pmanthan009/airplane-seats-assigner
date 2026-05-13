/**
 * AirlineSystem
 
 * PURPOSE: Manages flights, loads flight data, processes transactions, and prints results.
 */

package com.airline.system.model;
import java.io.*;

public class AirlineSystem {
    private FlightOrderedList flights = new FlightOrderedList();

    /**
     * AirlineSystem constructor
     * param: none
     * initializes a new AirlineSystem and prints a creation message
     * return type: void
     **/
    public AirlineSystem() {
        System.out.println("\n--------NEW AIRLINE SYSTEM CREATED--------\n");
    }

    /**
     * loadFlights method
     * param: flightsFilename (String)
     * reads flight definitions from the given file and inserts Flight objects into the ordered flight list
     * return type: void
     **/
    public void loadFlights(String flightsFilename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(flightsFilename));
        String line = null;
        boolean eof = false;

        while (!eof) {
            line = br.readLine();
            if (line == null) {
                eof = true;
            } else {
                line = line.trim();
                boolean emptyLine = false;
                if (line.length() == 0) {
                    emptyLine = true;
                }

                // skip blank lines (read until a non-empty line or EOF)
                while (emptyLine && !eof) {
                    line = br.readLine();
                    if (line == null) {
                        eof = true;
                    } else {
                        line = line.trim();
                        if (line.length() == 0) {
                            emptyLine = true;
                        } else {
                            emptyLine = false;
                        }
                    }
                }

                // if not EOF and we have a non-empty line, process a flight
                if (!eof && !emptyLine) {
                    int flightNo = Integer.parseInt(line);

                    String date = br.readLine();
                    if (date == null) {
                        // unexpected EOF — mark EOF and stop processing further flights
                        eof = true;
                    } else {
                        date = date.trim();

                        String scountLine = br.readLine();
                        if (scountLine == null) {
                            // unexpected EOF — mark EOF and stop processing further flights
                            eof = true;
                        } else {
                            scountLine = scountLine.trim();
                            int sectionsCount = Integer.parseInt(scountLine);
                            Section[] sections = new Section[sectionsCount];
                            int baseRow = 1;
                            int i;
                            for (i = 0; i < sectionsCount; i++) {
                                String spec = br.readLine();
                                if (spec == null) {
                                    // unexpected EOF — mark EOF and stop building sections
                                    eof = true;
                                } else {
                                    spec = spec.trim();
                                    String[] parts = spec.split("\\s+");
                                    int rows = Integer.parseInt(parts[0]);
                                    int[] blocks = new int[parts.length - 1];
                                    int j;
                                    for (j = 1; j < parts.length; j++) {
                                        blocks[j - 1] = Integer.parseInt(parts[j]);
                                    }
                                    
                                    // Read emergency exit row data
                                    String emergencyLine = br.readLine();
                                    if (emergencyLine == null) {
                                        eof = true;
                                    } else {
                                        emergencyLine = emergencyLine.trim();
                                        boolean[] emergencyRows = new boolean[rows];
                                        String[] emergencyParts = emergencyLine.split("\\s+");
                                        if (!emergencyParts[0].equals("0")) {
                                            for (String emergencyStr : emergencyParts) {
                                                int emergencyRow = Integer.parseInt(emergencyStr) - 1; // Convert to 0-based
                                                if (emergencyRow >= 0 && emergencyRow < rows) {
                                                    emergencyRows[emergencyRow] = true;
                                                }
                                            }
                                        }
                                        sections[i] = new Section(rows, blocks, baseRow, emergencyRows);
                                        baseRow = baseRow + rows;
                                    }
                                }
                            }
                            if (!eof) {
                                Flight f = new Flight(flightNo, date, sections);
                                flights.insertOrdered(f);
                            }
                        }
                    }
                }
            }
        }

        br.close();
    }


    /**
     * processTransactions method
     * param: txFilename (String)
     * creates a TransactionProcessor and processes the transactions file
     * return type: void
     **/
    public void processTransactions(String txFilename) throws IOException {
        TransactionProcessor tp = new TransactionProcessor(this);
        tp.processFile(txFilename);
    }

    /**
     * findFlight method
     * param: flightNo (int)
     * looks up and returns a Flight by its flight number from the ordered flight list
     * return type: Flight (or null if not found)
     **/
    public Flight findFlight(int flightNo) {
        return flights.find(flightNo);
    }

    /**
     * printAll method
     * param: none
     * prints seating charts and manifests for all flights in order
     * return type: void
     **/
    public void printAll() {
        Object[] arr = flights.toArray();
        int i = 0;
        while (i < arr.length) {
            Flight f = (Flight)arr[i];
            f.printSeatingChart();
            f.printManifest();
            System.out.println();
            i++;
        }
    }

    /**
     * insertFlight method
     * param: f (Flight)
     * inserts a Flight into the ordered flight list
     * return type: void
     **/
    public void insertFlight(Flight f) {
        flights.insertOrdered(f);
    }

    /**
     * getAllFlights method
     * Returns an array containing all loaded Flight objects sequentially.
     * return type: Object[]
     **/
    public Object[] getAllFlights() {
        return flights.toArray();
    }
}