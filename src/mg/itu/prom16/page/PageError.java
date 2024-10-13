package mg.itu.prom16.page;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

public class PageError {
    private String title;
    private String body;
    private Integer statusCode;
    private PrintWriter writer;

    public PageError() {
    }

    public void showPage() throws Exception {
        if(writer == null) throw new Exception("Writer is not set on PageError");

        String html =
        """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>:title</title>
                <style>
                    .header {
                        width: 100%;
                    }
                </style>
            </head>
            <body>
               <div class="header">
                    :error
               </div>
               <div class="content">
                    :body
               </div>
            </body>
            </html>
        """
            .replaceAll(":title", title != null ? title : "Error")
            .replaceAll(":error", "My Error")
            .replaceAll(":body", body);
        writer.write(html);
        writer.close();
    }

    public void close() {
        if(writer != null) writer.close();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(HttpServletResponse response) throws Exception {
        this.setWriter(response.getWriter());
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}
