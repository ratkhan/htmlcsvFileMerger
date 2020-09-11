package com.company;

import java.io.IOException;

/**
 * Parser interface
 * in case if you need to add support for new file formate (.xml ...etc)
 * ParserXML has to implement given interface (add given Parser to a factory method
 */
public interface Parser{
    public String[] readLine() throws IOException;
    public String[] readHeader() throws IOException;
    public boolean hasNextLine() throws IOException;
    public void close() throws Exception;
}
