package models;

import helpers.*;
import javafx.scene.paint.*;
import javafx.util.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SequenceModel {

    private final Map<Integer, AminoAcidModel> aminoAcids;
    private final Map<Integer, BondModel> connectionBonds;
    private final Map<Integer, BondModel> bonds;
    private final String id;
    private final String sequence;
    private final Color color;

    SequenceModel(String id, Map<Integer, AminoAcidModel> aminoAcids) {
        this.id = id;
        this.aminoAcids = aminoAcids;
        this.connectionBonds = createAminoAcidBonds(aminoAcids);
        this.bonds = collectBonds();
        this.sequence = createSequence(aminoAcids);

        Random random = new Random();
        this.color = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    private static Map<Integer, BondModel> createAminoAcidBonds(Map<Integer, AminoAcidModel> aminoAcidModels) {
        return aminoAcidModels.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(entry -> aminoAcidModels.containsKey(entry.getKey() + 1))
                .map(entry -> new Pair<>(entry.getValue()
                        .getAtomNameMap(), aminoAcidModels.get(entry.getKey() + 1)
                        .getAtomNameMap()))
                .filter(pair -> pair.getKey()
                        .containsKey("C") && pair.getValue()
                        .containsKey("N"))
                .map(pair -> new BondModel(pair.getKey()
                        .get("C"), pair.getValue()
                        .get("N")))
                .collect(Collectors.toMap(BondModel::getId, Function.identity()));
    }

    private static String createSequence(Map<Integer, AminoAcidModel> aminoAcidModels) {
        return aminoAcidModels.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(AminoAcidModel::getComponent)
                .map(AminoAcid::code)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private Map<Integer, BondModel> collectBonds() {
        Stream<BondModel> connections = connectionBonds.values()
                .stream();

        Stream<BondModel> aminoAcidBonds = aminoAcids.values()
                .stream()
                .map(AminoAcidModel::getBonds)
                .flatMap(List::stream);

        return Stream.concat(connections, aminoAcidBonds)
                .collect(Collectors.toMap(BondModel::getId, Function.identity()));
    }

    public String getId() {
        return id;
    }

    public Map<Integer, AminoAcidModel> getAminoAcids() {
        return aminoAcids;
    }

    public Map<Integer, BondModel> getBonds() {
        return bonds;
    }

    public String getSequence() {
        return sequence;
    }

    public Color getColor() {
        return color;
    }
}
