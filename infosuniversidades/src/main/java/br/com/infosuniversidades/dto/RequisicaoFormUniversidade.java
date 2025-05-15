package br.com.infosuniversidades.dto;

import br.com.infosuniversidades.models.Curso;
import br.com.infosuniversidades.models.Endereco;
import br.com.infosuniversidades.models.TipoUniversidade;
import br.com.infosuniversidades.models.Universidade;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class RequisicaoFormUniversidade {

    @NotBlank
    private String nome;
    @NotBlank
    private String site;
    private TipoUniversidade tipoUniversidade;
    private List<Endereco> enderecos = new ArrayList<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public TipoUniversidade getTipoUniversidade() {
        return tipoUniversidade;
    }

    public void setTipoUniversidade(TipoUniversidade tipoUniversidade) {
        this.tipoUniversidade = tipoUniversidade;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    public Universidade toUniversidade() {
        Universidade universidade = new Universidade();
        universidade.setNome(this.nome);
        universidade.setSite(this.site);
        universidade.setTipo(this.tipoUniversidade);
        universidade.setEnderecos(enderecos);

        return universidade;
    }

    public Universidade toUniversidade(Universidade universidade, List<Endereco> enderecos) {
        universidade.setNome(this.nome);
        universidade.setSite(this.site);
        universidade.setTipo(this.tipoUniversidade);
        universidade.setEnderecos(enderecos);

        return universidade;
    }

    public void fromUniversidade(Universidade universidade, List<Endereco> enderecos) {
        this.nome = universidade.getNome();
        this.site = universidade.getSite();
        this.tipoUniversidade = universidade.getTipo();
        this.enderecos.addAll(enderecos);
    }
}
