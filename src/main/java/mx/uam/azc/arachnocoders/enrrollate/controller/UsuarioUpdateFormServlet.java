package mx.uam.azc.arachnocoders.enrrollate.controller;

import javax.naming.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

import mx.uam.azc.arachnocoders.enrrollate.data.UsuarioDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class UsuarioUpdateFormServlet
 */
/**
 * @author ArachnoCoders
 */
@WebServlet(name = "UsuarioUpdateForm", urlPatterns = { "/UsuarioUpdateForm" })
public class UsuarioUpdateFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pattern = request.getParameter("pattern");
        if (pattern == null || pattern.isEmpty()) {
            pattern = "%"; // Si no hay patrón, usar '%' para obtener todos los usuarios
        }

        try {
            List<UsuarioDTO> usuarios = getUsuario(pattern);
            request.setAttribute("usuarios", usuarios);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/usuarios.jsp");
        dispatcher.forward(request, response);
    }

    private List<UsuarioDTO> getUsuario(String pattern) throws NamingException, SQLException {
        Context context = new InitialContext();
        DataSource source = (DataSource) context.lookup("java:comp/env/jdbc/TestDS");
        Connection connection = source.getConnection();
        try {
            return getUsuario(connection, pattern);
        } finally {
            connection.close();
        }
    }

    private List<UsuarioDTO> getUsuario(Connection connection, String pattern) throws SQLException {
        String query = "SELECT id_usuario, nombre, email, dirección, teléfono, rol, fecha_registro FROM usuarios WHERE nombre LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, pattern);
            ResultSet cursor = statement.executeQuery();
            List<UsuarioDTO> usuarios = new ArrayList<UsuarioDTO>();
            while (cursor.next()) {
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setIdUsuario(cursor.getString(1));
                usuario.setNombre(cursor.getString(2));
                usuario.setEmail(cursor.getString(3));
                usuario.setDireccion(cursor.getString(4));
                usuario.setTelefono(cursor.getString(5));
                usuario.setRol(cursor.getString(6));
                usuario.setFechaRegistro(cursor.getString(7));
                usuarios.add(usuario);
            }
            return usuarios;
        }
    }
}
