package ni.edu.uam.demologin.service;

public class ValidationService {

    //Para usar solo pueden usar un if(Validar() == false) seguido a lo que pasa

    public boolean validarEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); //el patrón de expresión regular para validar un correo electrónico
    }
    public boolean validarUsuario(String usuario) {
        return usuario != null && usuario.matches("^[A-Za-z0-9]{3,20}$");
    }
    public boolean validarPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*[0-9]).{8,}$");
        //aqui el patrón de expresión regular para validar una contraseña que contenga al menos 8 caracteres,
        // incluyendo al menos una letra y un número
    }
    public boolean validarNombre(String nombre) {
        return nombre != null && nombre.matches("^[A-Za-z]{2,50}$");
    }
    public boolean validarApellido(String apellido) {
        return apellido != null && apellido.matches("^[A-Za-z]{2,50}$");
    }
}
