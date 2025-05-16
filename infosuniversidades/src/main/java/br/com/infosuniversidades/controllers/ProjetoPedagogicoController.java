package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormProjetoPedagogico;
import br.com.infosuniversidades.models.Curso;
import br.com.infosuniversidades.models.ProjetoPedagogico;
import br.com.infosuniversidades.models.Universidade;
import br.com.infosuniversidades.repositories.CursoRepository;
import br.com.infosuniversidades.repositories.ProjetoPedagogicoRepository;
import br.com.infosuniversidades.repositories.UniversidadeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProjetoPedagogicoController {

    @Autowired
    private ProjetoPedagogicoRepository projetoPedagogicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @GetMapping("/{cursoId}/{universidadeId}/cadastro")
    public ModelAndView novoProjetoPedagogico(@PathVariable Long cursoId, @PathVariable Long universidadeId, RequisicaoFormProjetoPedagogico requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("mensagem", "É necessário ter um cargo de administrador para adicionar um novo plano pedagógico");
        } else {
            ModelAndView mv = new ModelAndView("cadastro-projeto-pedagogico");

            return mv;
        }
    }

    @PostMapping("/{cursoId}/{universidadeId}/cadastro")
    public ModelAndView cadastroProjetoPedagogico(@PathVariable Long cursoId, @PathVariable Long universidadeId, @Valid RequisicaoFormProjetoPedagogico requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("mensagem", "É necessário ter um cargo de administrador para adicionar um novo plano pedagógico");
        } else {
            Optional<Curso> optionalCurso = this.cursoRepository.findById(cursoId);
            Optional<Universidade> optionalUniversidade = this.universidadeRepository.findById(universidadeId);
            ProjetoPedagogico projetoPedagogico = new ProjetoPedagogico();
            projetoPedagogico.setLink(requisicao.getLink());
            if (optionalCurso.isPresent()) {
                projetoPedagogico.setCurso(optionalCurso.get());
            }
            if (optionalUniversidade.isPresent()) {
                projetoPedagogico.setUniversidade(optionalUniversidade.get());
            }
            this.projetoPedagogicoRepository.save(projetoPedagogico);

            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("sucesso", "Projeto pedagógico cadastrado com sucesso");
        }
    }

}
