package net.atomique.ksar;

import java.util.StringTokenizer;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public interface IOsSpecificParser {

    void parseOsInfo(StringTokenizer matcher);
    
    void parse(final String line, String firstToken, StringTokenizer matcher) throws ParsingException;
}
