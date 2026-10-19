package ni.edu.uam.demologin.repository;

import ni.edu.uam.demologin.config.IConnection;
import ni.edu.uam.demologin.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsuarioRepositoryJdbc implements UsuarioRepository {

    private final IConnection connection;

    public UsuarioRepositoryJdbc(IConnection connection) {
        this.connection = connection;
    }

    // Consulta todos los usuarios registrados.
    @Override
    public List<Usuario> findAll() throws Exception {

        String sql = "SELECT id, primer_nombre, primer_apellido, " +
                "ruta_foto, usuario, email, password " +
                "FROM usuario " +
                "ORDER BY primer_nombre ASC";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Usuario usuario = new Usuario(
                        rs.getString("primer_nombre"),
                        rs.getString("primer_apellido"),
                        rs.getString("email"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getString("ruta_foto")
                );

                // Conserva el identificador original de la base de datos.
                usuario.setId(rs.getObject("id", UUID.class));

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    // Busca un usuario por su identificador.
    @Override
    public Optional<Usuario> findById(String id) throws Exception {
        String sql = "SELECT id, primer_nombre, primer_apellido, " +
                "ruta_foto, usuario, email, password " +
                "FROM usuario " +
                "WHERE id = ?";

        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getString("primer_nombre"),
                            rs.getString("primer_apellido"),
                            rs.getString("email"),
                            rs.getString("usuario"),
                            rs.getString("password"),
                            rs.getString("ruta_foto")
                    );
                    usuario.setId(rs.getObject("id", UUID.class));
                    return Optional.of(usuario);
                }
            }
        }
        return Optional.empty();
    }

    // Registra un usuario nuevo.
    @Override
    public void save(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuario " +
                "(id, primer_nombre, primer_apellido, usuario, " +
                "password, email, ruta_foto) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            // Genera un identificador único para el nuevo registro.
            UUID id = UUID.randomUUID();
            ps.setObject(1, id);
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getUsuario());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getCorreo());
            ps.setString(7, usuario.getRutafoto());
            ps.executeUpdate();
            // Mantiene el ID generado en el objeto.
            usuario.setId(id);
        }
    }
    // Actualiza los datos de un usuario existente.
    @Override
    public void update(Usuario usuario) throws Exception {

        String sql = "UPDATE usuario SET " +
                "primer_nombre = ?, " +
                "primer_apellido = ?, " +
                "usuario = ?, " +
                "password = ?, " +
                "email = ?, " +
                "ruta_foto = ? " +
                "WHERE id = ?";

        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());
            ps.setString(5, usuario.getCorreo());
            ps.setString(6, usuario.getRutafoto());
            ps.setObject(7, usuario.getId());
            ps.executeUpdate();
        }
    }
    // Elimina un usuario por su identificador.
    @Override
    public void delete(UUID id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        }
    }
    // Busca un usuario por sus credenciales de inicio de sesión.
    @Override
    public Optional<Usuario> findByLogin(
            String usuario, String password) throws Exception {
        String sql = "SELECT id, usuario, email, password " +
                "FROM public.usuario " +
                "WHERE usuario = ? AND password = ?";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getObject("id", UUID.class));
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setCorreo(rs.getString("email"));
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }
}
