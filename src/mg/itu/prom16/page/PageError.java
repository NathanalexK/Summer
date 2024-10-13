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

    public static void showPage(HttpServletResponse response, Integer statusCode, String body) {
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (Exception e) {
            System.err.println("PrintWriter not found");
            return;
        }

        String html =
        """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>:title</title>
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                    }
                    .header {
                        width: 100%;
                        background-color: #518b8d;
                        color: white;
                        padding: 5px 20px;
                        font-family: fantasy;
                        font-size: 24px;
                    }
                    .content {
                        font-family: cursive;
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
            .replaceAll(":title", "Error")
            .replaceAll(":error", "Erreur " + statusCode + ": ")
            .replaceAll(":body", body);
        out.write(html);
        out.close();
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
