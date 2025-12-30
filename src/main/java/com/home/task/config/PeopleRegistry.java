package com.home.task.config;

import com.home.task.dto.PersonDTO;

import java.util.List;

public class PeopleRegistry {

    public static List<PersonDTO> allPeople() {

        return List.of(
                person("Akash", true),
                person("Varun", true),
                person("Ankith", true),
                person("Jaswanth", true),
                person("Abbas", false),
                person("Naveen", false)
        );
    }

    private static PersonDTO person(String name, boolean canCook) {
        PersonDTO p = new PersonDTO();
        p.setName(name);
        p.setCanCook(canCook);
        p.setAvailable(false); // default
        return p;
    }
}
