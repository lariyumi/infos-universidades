package br.com.infosuniversidades.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RequisicaoPerfilUsuario {

    @NotBlank
    private String nome;
    @Email
    @NotBlank
    private String email;

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

    @Override
    public String toString() {
        return "RequisicaoFormPerfil{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
