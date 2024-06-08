import seminar.seminar2.g1064.Agent;
import seminar.seminar2.g1064.Apartament;
import seminar.seminar2.g1064.Imobil;
import seminar.seminar2.g1064.Zona;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        seminar.seminar2.g1064.Main.citireDate("apartamente.csv");
        List<Agent> agenti = seminar.seminar2.g1064.Main.agenti;
        List<Apartament> apartamente = seminar.seminar2.g1064.Main.apartamente;
        agenti.forEach(System.out::println);
        apartamente.forEach(System.out::println);
        System.out.println("----------Cerinta 1----------------");
        apartamente.stream().filter(ap -> ap.getZona().equals(Zona.AVIATIEI)).forEach(System.out::println);
        System.out.println("----------Cerinta 2----------------");
        apartamente.stream().filter(ap -> ap.getPret() > 100_000 && ap.getPret() < 200_000).forEach(System.out::println);
        System.out.println("----------Cerinta 3----------------");
        apartamente.stream().filter(ap -> Arrays.stream(ap.getDotari()).filter(dotare -> dotare.equals("loc parcare")).count() == 1).forEach(System.out::println);
        System.out.println("----------Cerinta 4----------------");
        apartamente.stream().sorted(Comparator.comparingDouble(Imobil::getPret)).forEach(System.out::println);
        System.out.println("----------Cerinta 5----------------");
        long cnp = 1671010288599L;
        Set<String> numereDeTel = agenti.stream().filter(a -> a.getCnp() == cnp).flatMap(agent -> Arrays.stream(agent.getImobile()).boxed()).flatMap(idImobil -> apartamente.stream().filter(ap -> ap.getId() == idImobil)).map(Imobil::getTelefonP).collect(Collectors.toSet());

        numereDeTel.forEach(System.out::println);
        System.out.println("----------Cerinta 6----------------");
        Map<Integer, String> cerinta6 = apartamente.stream().collect(Collectors.toMap(Imobil::getId, Imobil::getTelefonP));
        cerinta6.forEach((key, value) -> System.out.println(key + " -> " + value));
        Map<Integer, String> sortedMap = cerinta6.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println("Sortata");
        sortedMap.forEach((v, k) -> {
            System.out.println(v + " -> " + k);
        });
        System.out.println("----------Cerinta 7----------------");
        Map<Zona, List<Apartament>> cerinta7 = apartamente.stream().collect(Collectors.groupingBy(Imobil::getZona, Collectors.toList()));
        cerinta7.forEach((v, k) -> {
            System.out.println(v + " " + k);
        });

        System.out.println("----------Cerinta 8----------------");
        Map<Zona, List<Integer>> cerinta8 = apartamente.stream().collect(Collectors.groupingBy(Imobil::getZona, Collectors.mapping(Imobil::getId, Collectors.toList())));

        cerinta8.forEach((v, k) -> {
            System.out.println(v + " " + k);
        });

        System.out.println("----------Cerinta 9----------------");
        Map<Zona, Double> cerinta9 = apartamente.stream().collect(Collectors.groupingBy(Imobil::getZona, Collectors.averagingDouble(Apartament::getPret)));
        cerinta9.forEach((v, k) -> {
            System.out.println(v + " " + k);
        });
        System.out.println("----------Cerinta 10----------------");
        Map<Integer, ?> cerinta10 = apartamente.stream().collect(Collectors.toMap(Imobil::getId, apartament -> new Object() {
            Zona zona = apartament.getZona();
            double pret = apartament.getPret();
            int suprafata = apartament.getSuprafataUtila();

            @Override
            public String toString() {
                return zona + "," + pret / suprafata;
            }
        }));
        cerinta10.forEach((v, k) -> {
            System.out.println(v + " " + k);
        });

        System.out.println("----------Cerinta 11----------------");
        Map<Long, Set<String>> cerinta11 = agenti.stream().collect(
                HashMap::new,
                (longHash, agent) -> {
                    Set<String> telefonane = new HashSet<>();
                    Arrays.stream(agent.getImobile()).forEach(id -> {
                        telefonane.add(apartamente.stream().filter(a -> a.getId() == id).map(Imobil::getTelefonP).findAny().orElse(""));
                    });
                    longHash.put(agent.getCnp(), telefonane);
                }
                , HashMap::putAll
        );
        cerinta11.forEach((k, v) -> {
            System.out.println(k + "->" + v);
        });


    }
}