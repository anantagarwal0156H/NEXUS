package com.nexus.domain;
import java.util.*;
public final class CampusCatalog {
    private CampusCatalog() {}
    public record Site(String code,String name,Double lat,Double lng) {}
    public static final List<Site> SITES=List.of(
        new Site("medical","Medical Centre",17.542,78.5764),
        new Site("sac","Student Activity Centre",17.5409,78.5753),
        new Site("mess1","Mess 1",17.5424,78.5738),
        new Site("mess2","Mess 2",17.5449,78.5752),
        new Site("library","Library / G Block area",17.5458,78.5715),
        new Site("cp","Connaught Place",17.542,78.5758),
        new Site("auditorium","Auditorium",17.5455,78.5709),
        new Site("fountain","Fountain",17.5469,78.5721),
        new Site("lane","Lover’s Lane",17.5468,78.573),
        new Site("road","Road Not Taken",17.5408,78.574),
        new Site("pool","Swimming Pool",17.5402,78.5768),
        new Site("garden","Rock Garden",17.5445,78.573),
        new Site("football-old","Old Football Ground",17.5436,78.575),
        new Site("football-new","New Football Ground",17.541,78.5762),
        new Site("cricket","Cricket Ground",17.5398,78.5773),
        new Site("volleyball","Volleyball Court",17.543,78.5753),
        new Site("new-acad","New Academic Block / Chess Courtyard",17.5459,78.5696),
        new Site("lawns","Library Lawns",17.5452,78.5712),
        new Site("valmiki","Valmiki Bhavan",17.5458,78.5747),
        new Site("gautam","Gautam Bhavan",17.5416,78.5752),
        new Site("krishna-ram","Krishna / Ram Bhavan",17.5427,78.574),
        new Site("gandhi","Gandhi Bhavan",17.5425,78.5743),
        new Site("vk","Vishwakarma Bhavan",17.545,78.5764),
        new Site("shankar","Shankar Bhavan",17.5447,78.5749),
        new Site("vyas","Vyas Bhavan",17.5445,78.5753),
        new Site("malviya","Malviya Bhavan",17.541,78.5748),
        new Site("meera","Meera Bhavan",17.5416,78.574),
        new Site("ganga","Ganga Bhavan",17.5419,78.5734),
        new Site("b","B Block",null,null),
        new Site("c","C Block",null,null),
        new Site("d","D Block",null,null),
        new Site("f","F Block",null,null),
        new Site("g","G Block",null,null));
    public static Site require(String code) {
        return SITES.stream().filter(s->s.code().equals(code)).findFirst().orElseThrow(()->new IllegalArgumentException("Unknown campus location"));
    }
}
