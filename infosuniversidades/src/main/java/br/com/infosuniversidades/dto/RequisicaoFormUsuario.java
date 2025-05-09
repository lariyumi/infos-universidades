package br.com.infosuniversidades.dto;

import br.com.infosuniversidades.models.TipoUsuario;
import br.com.infosuniversidades.models.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RequisicaoFormUsuario {

    @NotBlank
    private String nome;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    private TipoUsuario tipoUsuario;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String senhaCriptografada = encoder.encode(this.senha);
        usuario.setSenha(senhaCriptografada);
        usuario.setTipo(TipoUsuario.COMUM);

        return usuario;
    }

    public Usuario toUsuario(Usuario usuario) {
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String senhaCriptografada = encoder.encode(this.senha);
        usuario.setSenha(senhaCriptografada);
        usuario.setTipo(TipoUsuario.COMUM);

        return usuario;
    }

    @Override
    public String toString() {
        return "RequisicaoFormUsuario{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
