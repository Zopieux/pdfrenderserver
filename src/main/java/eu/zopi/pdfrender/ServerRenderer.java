package eu.zopi.pdfrender;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "renderer", urlPatterns = {"/"})
public class ServerRenderer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final ServletOutputStream stream = resp.getOutputStream();
        final ITextRenderer renderer = new ITextRenderer();

        final Document document = Jsoup.parse(
                req.getInputStream(),
                StandardCharsets.UTF_8.name(),
                "http://none/");
        // fix doctype
        Element tag = document.getElementsByTag("html").get(0);
        tag.before(new DocumentType("html",
                "-//W3C//DTD XHTML 1.0 Transitional//EN",
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
                document.baseUri()));
        document.outputSettings(new Document.OutputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false));
        String html = document.outerHtml();

        renderer.setDocumentFromString(html);
        try {
            renderer.layout();
            renderer.createPDF(stream);
        } catch (Exception e) {
            PrintWriter writer = new PrintWriter(stream);
            e.printStackTrace(writer);
            writer.close();
        }
    }
}
