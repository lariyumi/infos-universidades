package br.com.infosuniversidades.dto;

import br.com.infosuniversidades.models.Universidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class RequisicaoFormCurso {

    @NotBlank
    private String nome;
    @NotBlank
    private String descricao;
    @NotEmpty
    private List<Universidade> universidades = new ArrayList<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Universidade> getUniversidades() {
        return universidades;
    }

    public void setUniversidades(List<Universidade> universidades) {
        this.universidades = universidades;
    }

}
