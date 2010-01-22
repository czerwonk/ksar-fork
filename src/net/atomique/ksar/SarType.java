package net.atomique.ksar;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public enum SarType {
    Unknown(null),
    SunOS("SunOS"),
    Linux("Linux"),
    AIX("AIX"),
    HP_UX("HP-UX"),
    Darwin("Darwin"),
    Esar("Esar");
    
    
    SarType(String parsingString) {
        this.parsingString = parsingString;
    }
    
    
    private final String parsingString;
    
    public String getParsingString() {
        return this.parsingString;
    }
}
