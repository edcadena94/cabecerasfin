package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import models.Producto;
import service.ProductoService;
import service.ProductoServiceImplement;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet({"/productos.xls", "/productos.html"})
public class ProductoXlsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ProductoService service = new ProductoServiceImplement();
        List<Producto> productos = service.listar();

        String servletPath = req.getServletPath();
        String format = req.getParameter("format");
        boolean esXls = servletPath.endsWith(".xls");
        boolean esJson = "json".equalsIgnoreCase(format);

        // --- RESPUESTA JSON DESCARGABLE ---
        if (esJson) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=productos.json");
            try (PrintWriter out = resp.getWriter()) {
                out.println("[");
                for (int i = 0; i < productos.size(); i++) {
                    Producto p = productos.get(i);
                    out.println("  {");
                    out.println("    \"id\": " + p.getId() + ",");
                    out.println("    \"nombre\": \"" + p.getNombre() + "\",");
                    out.println("    \"tipo\": \"" + p.getTipo() + "\",");
                    out.println("    \"precio\": " + p.getPrecio());
                    if (i < productos.size() - 1) {
                        out.println("  },");
                    } else {
                        out.println("  }");
                    }
                }
                out.println("]");
            }
            return;
        }

        // --- RESPUESTA EXCEL ---
        if (esXls) {
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment; filename=productos.xls");
        } else {
            resp.setContentType("text/html;charset=UTF-8");
        }

        // --- RESPUESTA HTML O XLS ---
        try (PrintWriter out = resp.getWriter()) {
            if (!esXls) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset=\"utf-8\">");
                out.println("<title>Listado de Productos</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Listado de productos</h1>");
                out.println("<p><a href=\"" + req.getContextPath() + "/productos.xls\">Exportar a Excel</a></p>");
                out.println("<p><a href=\"" + req.getContextPath() + "/productos.html?format=json\">Descargar JSON</a></p>");
            }

            out.println("<table>");
            out.println("<tr>");
            out.println("<th>id</th>");
            out.println("<th>nombre</th>");
            out.println("<th>tipo</th>");
            out.println("<th>precio</th>");
            out.println("</tr>");

            productos.forEach(p -> {
                out.println("<tr>");
                out.println("<td>" + p.getId() + "</td>");
                out.println("<td>" + p.getNombre() + "</td>");
                out.println("<td>" + p.getTipo() + "</td>");
                out.println("<td>" + p.getPrecio() + "</td>");
                out.println("</tr>");
            });

            out.println("</table>");

            if (!esXls) {
                out.println("</body>");
                out.println("</html>");
            }
        }
    }
}
