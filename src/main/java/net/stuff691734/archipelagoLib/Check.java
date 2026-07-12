package net.stuff691734.archipelagoLib;

public class Check implements Comparable<Check> {
    public String page;
    public String type;
    public DependencyNotation dependencies;

    public Check(String type, DependencyNotation dependencies, String page) {
        this.type = type;
        this.dependencies = dependencies;
        this.page = page;
    }

    @Override
    public int compareTo(Check check) {
        return this.page.compareTo(check.page);
    }
}