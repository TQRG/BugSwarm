package de.is24.deadcode4j.analyzer.hibernateannotations;

import org.hibernate.annotations.Type;

public class Entity {

    @Type(type = "byteClass")
    private Number id;

}
