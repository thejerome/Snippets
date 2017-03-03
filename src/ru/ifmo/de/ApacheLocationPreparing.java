package ru.ifmo.de;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ApacheLocationPreparing {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        List<String> domains = new ArrayList<>();
        String domain;
        while (!(domain = br.readLine()).equals("fin")){
            domains.add(domain);
        }

        //Substitute "s|https://([\w\d\.-]*annualreviews.org)|https://$1.de.ifmo.ru|i"
        domains.stream().sequential().map(
                d -> "                Substitute \"s|https://" + d + ".de.ifmo.ru|https://" + d.replace(".", "--") + ".de.ifmo.ru|ni\""
        ).forEach(System.out::println);

    }
}
