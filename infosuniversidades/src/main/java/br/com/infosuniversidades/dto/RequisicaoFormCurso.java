package br.com.infosuniversidades.dto;

import br.com.infosuniversidades.models.ProjetoPedagogico;
import br.com.infosuniversidades.models.Universidade;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class RequisicaoFormCurso {

    @NotBlank
    private String nome;
    @NotBlank
    private String descricao;
//    private List<Universidade> universidade = new ArrayList<>();
    private List<ProjetoPedagogico> projetosPedagogicos = new ArrayList<>();

}
