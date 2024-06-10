import seminar.seminar2.g1064.Agent;
import seminar.seminar2.g1064.Apartament;
import seminar.seminar2.g1064.Imobil;
import seminar.seminar2.g1064.Zona;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        seminar.seminar2.g1064.Main.citireDate("apartamente.csv");
        List<Agent> agenti = seminar.seminar2.g1064.Main.agenti;
        List<Apartament> apartamente = seminar.seminar2.g1064.Main.apartamente;
        agenti.forEach(System.out::println);
        apartamente.forEach(System.out::println);
        System.out.println("----------Cerinta 1----------------");
        apartamente.stream().filter(a -> a.getZona().equals(Zona.AVIATIEI)).forEach(System.out::println);
        System.out.println("----------Cerinta 2----------------");
        apartamente.stream().filter(a -> a.getPret() < 200_000 && a.getPret() > 100_000).forEach(System.out::println);

        System.out.println("----------Cerinta 3----------------");
        apartamente.stream().filter(a -> Arrays.asList(a.getDotari()).contains("loc parcare")).forEach(System.out::println);
        System.out.println("----------Cerinta 4----------------");
        apartamente.stream().sorted(Comparator.comparingDouble(Apartament::getPret).reversed()).forEach(System.out::println);
        System.out.println("----------Cerinta 5----------------");
        long cnp = 1680909234564L;
        Agent agent = agenti.stream().filter(a -> a.getCnp() == cnp).findFirst().orElse(null);
        if (agent != null) {
            var set = Arrays.stream(agent.getImobile()).mapToObj(id -> apartamente.stream().filter(a -> a.getId() == id).map(Imobil::getTelefonP).findFirst().get()).collect(Collectors.toSet());
            set.forEach(System.out::println);
        }
        System.out.println("----------Cerinta 6----------------");
        Map<Integer, String> cerinta6 = apartamente.stream().collect(Collectors.toMap(Imobil::getId, Imobil::getTelefonP));
        cerinta6.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("----------Cerinta 7----------------");
        Map<Zona, List<Apartament>> cerinta7 = apartamente.stream().collect(Collectors.groupingBy(Apartament::getZona,
                Collectors.toList()
        ));
        cerinta7.forEach((k, v) -> {
            System.out.println("Pentru zona " + k);
            v.forEach(System.out::println);
        });

        System.out.println("----------Cerinta 8----------------");
        Map<Zona, List<Integer>> cerinta8 = apartamente.stream().collect(Collectors.groupingBy(Apartament::getZona,
                Collectors.mapping(Imobil::getId, Collectors.toList())
        ));
        cerinta8.forEach((k, v) -> {
            System.out.println("Pentru zona " + k);
            v.forEach(System.out::println);
        });
        System.out.println("----------Cerinta 9----------------");
        Map<Zona, Double> cerinta9 = apartamente.stream().collect(Collectors.groupingBy(Imobil::getZona,
                Collectors.averagingDouble(Imobil::getPret)));
        cerinta9.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });

        System.out.println("----------Cerinta 10----------------");

        Map<Integer, ?> cerinta10 = apartamente.stream().collect(Collectors.toMap(Imobil::getId, a -> new Object() {
            Zona zona = a.getZona();
            double pret = a.getPret();

            @Override
            public String toString() {
                return  "Zona: " + zona + " pret: " + pret;
            }
        }));

        cerinta10.forEach((v,k)-> System.out.println(v + " " + k));
        System.out.println("----------Cerinta 11----------------");

        Map<Long, Set<String>> cerinta11 = agenti.stream().collect(
                HashMap::new,
                new BiConsumer<HashMap<Long, Set<String>>, Agent>() {
                    @Override
                    public void accept(HashMap<Long, Set<String>> longSetHashMap, Agent agent) {
                       Set<String> nrDeTel = apartamente.stream().filter(a-> Arrays.stream(agent.getImobile()).filter(ag->ag == a.getId()).count() > 0)
                                .map(Imobil::getTelefonP)
                                .collect(Collectors.toSet());
                       longSetHashMap.put(agent.getCnp(), nrDeTel);
                    }
                },
                HashMap::putAll
        );

        cerinta11.forEach((k,v)-> System.out.println(k + " " + v));

    }
}