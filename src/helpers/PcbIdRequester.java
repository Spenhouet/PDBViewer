package helpers;

import exceptions.*;
import javafx.concurrent.*;
import models.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class PcbIdRequester {

    public static final String FILENAME = "pdbList.csv";

    private PcbIdRequester() {
        //hide constructor
    }

    public static Task<Void> downloadAndSaveList() {
        return new Task<Void>() {
            @Override
            public Void call() throws PdbIdRetrieveException {
                String url = "http://www.rcsb.org/pdb/rest/customReport.csv?pdbids=*&customReportColumns=structureId,macromoleculeType,classification,structureTitle&format=csv&service=wsfile";

                //This is just an estimate:
                int totalPdbEntries = 140490;

                try (BufferedReader bufferedReader = new BufferedReader(Request.getFromURL(url))) {

                    File pdbListFile = new File(System.getProperty("java.io.tmpdir") + FILENAME);
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pdbListFile))) {
                        String line;
                        int entryCount = 0;
                        while ((line = bufferedReader.readLine()) != null) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                            updateProgress(++entryCount, totalPdbEntries);
                        }
                    }

                } catch (IOException e) {
                    throw new PdbIdRetrieveException();
                }
                return null;
            }
        };
    }

    public static Task<List<PdbId>> retrieveData() {
        return new Task<List<PdbId>>() {
            @Override
            public List<PdbId> call() throws PdbIdRetrieveException {
                File pdbListFile = new File(System.getProperty("java.io.tmpdir") + FILENAME);

                long fileLength = pdbListFile.length();

                if (!pdbListFile.exists() || pdbListFile.isDirectory())
                    return Collections.emptyList();

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pdbListFile))) {
                    AtomicLong atomicLong = new AtomicLong(0);
                    return bufferedReader.lines()
                            .peek(line -> updateProgress(atomicLong.addAndGet(line.getBytes().length + 2L), fileLength))
                            .filter(line -> !line.isEmpty())
                            .filter(line -> !"structureId,macromoleculeType,classification,structureTitle".equals(line))
                            .map(line -> line.split("\",\"|\""))
                            .filter(tokens -> tokens.length == 5)
                            .filter(tokens -> "Protein".equals(tokens[2]))
                            .map(tokens -> new PdbId(tokens[1], tokens[3], tokens[4]))
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw new PdbIdRetrieveException();
                }
            }
        };
    }
}
