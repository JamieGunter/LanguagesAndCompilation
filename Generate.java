public class Generate extends AbstractGenerate {

    /**
     * I handled the complication exception message forming in the acceptTerminal
     * method. I have chosen this as I utilised a property from the current token as
     * well as the next token within my error message. This requires the lexical
     * analyser to be visible, which isn't possible within the Generate class,
     * besides just forming the message in the Syntax Analyser and parsing it
     * and losing access.
     * 
     * (This solution is more convienient to do within the Syntax Analyser)
     * 
     * The below method takes in the parsed pre-formed message and throws the
     * exception.
     **/

    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage);
    }

}