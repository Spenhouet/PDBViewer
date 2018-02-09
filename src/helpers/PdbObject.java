package helpers;

import exceptions.*;
import javafx.concurrent.*;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;
import java.util.stream.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class PdbObject {

    private static String pdbIdSave;
    private static Map<String, List<String[]>> elements;
    private static String descriptor = "";

    private PdbObject() {
        //hide constructor
    }

    public static Task<Void> loadPdb(Reader reader, String pdbId) {
        pdbIdSave = pdbId.toUpperCase();

        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final int numberAtoms = getAtomsCount(pdbId);

                try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    elements = bufferedReader.lines()
                            .parallel()
                            .filter(line -> !line.isEmpty())
                            .map(PdbFileElements::splitString)
                            .peek(c -> updateProgress(atomicInteger.incrementAndGet(), numberAtoms))
                            .collect(groupingBy(tokens -> tokens[0].trim()));
                } catch (IOException e) {
                    throw new PdbDataReadException();
                }

                if (elements.containsKey(PdbFileElements.KEYWDS.name())) {
                    List<String[]> keywdsList = elements.get(PdbFileElements.KEYWDS.name());
                    if (!keywdsList.isEmpty() && keywdsList.get(0).length > 1)
                        descriptor = keywdsList.get(0)[1].trim();
                }

                return null;
            }
        };
    }

    private static int getAtomsCount(String pdbId) {
        try (BufferedReader bufferedReader = new BufferedReader(Request.getFromURL("https://www.rcsb.org/pdb/rest/describePDB?structureId=" + pdbId))) {

            String xml = bufferedReader.lines()
                    .collect(Collectors.joining());
            Pattern pattern = Pattern.compile("nr_atoms=\"(.+?)\"");
            Matcher matcher = pattern.matcher(xml);

            if (!matcher.find())
                return -1;

            return Integer.parseInt(matcher.group(1));
        } catch (IOException e) {
            return -1;
        }
    }

    public static Map<String, Set<IndexRange>> getHelixSet() {
        return Optional.ofNullable(elements.get(PdbFileElements.HELIX.name()))
                .orElse(Collections.emptyList())
                .stream()
                .collect(groupingBy(helix -> helix[2].trim(), Collectors.mapping(helix -> new IndexRange(Integer.parseInt(helix[3].trim()), Integer.parseInt(helix[5].trim())), Collectors.toSet())));
    }

    public static Map<String, Set<IndexRange>> getSheetSet() {
        return Optional.ofNullable(elements.get(PdbFileElements.SHEET.name()))
                .orElse(Collections.emptyList())
                .stream()
                .collect(groupingBy(sheet -> sheet[2].trim(), Collectors.mapping(sheet -> new IndexRange(Integer.parseInt(sheet[3].trim()), Integer.parseInt(sheet[5].trim())), Collectors.toSet())));
    }

    public static Map<String, Map<Integer, Map<String, List<String[]>>>> getGroupedAtomData() {
        return elements.get(PdbFileElements.ATOM.name())
                .stream()
                .collect(groupingBy(tokens -> tokens[5].trim(), groupingBy(tokens -> Integer.parseInt(tokens[6].trim()), groupingBy(tokens -> tokens[7].trim()))));
    }

    public static String asString() {
        return elements.entrySet()
                .stream()
                .sorted(comparing(entry -> PdbFileElements.rank(entry.getKey())))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(arr -> String.join("", arr))
                .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    public static String getPdbId() {
        return pdbIdSave;
    }

    public static String getDescriptor() {
        return descriptor;
    }
}
