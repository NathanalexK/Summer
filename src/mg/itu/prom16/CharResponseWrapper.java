package mg.itu.prom16;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class CharResponseWrapper extends HttpServletResponseWrapper {
    private final CharArrayWriter charArrayWriter = new CharArrayWriter();
    private final PrintWriter writer = new PrintWriter(charArrayWriter);

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public String toString() {
        writer.flush();
        return charArrayWriter.toString();
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}
