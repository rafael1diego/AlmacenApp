package pe.ebenites.almacenapp.services;


import pe.ebenites.almacenapp.models.Usuario;

public interface AuthenticationService {

    Usuario findByUsernameAndPassword(String username, String password);

    Usuario findByUsername(String username);

}

