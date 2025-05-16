package br.com.infosuniversidades.dto;

import jakarta.validation.constraints.NotBlank;

public class RequisicaoFormProjetoPedagogico {

    @NotBlank
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
