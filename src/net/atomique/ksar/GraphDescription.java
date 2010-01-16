/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

/**
 *
 * @author alex
 */
public class GraphDescription {

    public GraphDescription(AllGraph o, String s, String s1, String s2) {
        objectPointer = o;
        className = s;
        description = s1;
        option = s2;
    }

    public AllGraph getobjectPointer() {
        return objectPointer;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }

    public String getOption() {
        return option;
    }

    public String toString() {
        return description;
    }
    private AllGraph objectPointer;
    private String className;
    private String description;
    private String option;
}
