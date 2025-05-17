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
import org.springframework.ui.Model;
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

    @GetMapping("/cursos/{cursoId}/universidade/{universidadeId}/cadastro")
    public ModelAndView novoProjetoPedagogico(@PathVariable Long cursoId, @PathVariable Long universidadeId, RequisicaoFormProjetoPedagogico requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("mensagem", "É necessário ter um cargo de administrador para adicionar um novo plano pedagógico");
        } else {
            ModelAndView mv = new ModelAndView("cadastro-projeto-pedagogico");

            return mv;
        }
    }

    @PostMapping("/cursos/{cursoId}/universidade/{universidadeId}/cadastro")
    public ModelAndView cadastroProjetoPedagogico(@PathVariable Long cursoId, @PathVariable Long universidadeId, @Valid RequisicaoFormProjetoPedagogico requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("mensagem", "É necessário ter um cargo de administrador para adicionar um novo projeto pedagógico");
        } else {
            Optional<Curso> optionalCurso = this.cursoRepository.findById(cursoId);
            Optional<Universidade> optionalUniversidade = this.universidadeRepository.findById(universidadeId);
            ProjetoPedagogico projetoPedagogico = new ProjetoPedagogico();
            projetoPedagogico.setLink(requisicao.getLink());
            if (optionalCurso.isPresent()) {
                projetoPedagogico.setCurso(optionalCurso.get());
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem", "Curso não encontrado");
            }
            if (optionalUniversidade.isPresent()) {
                projetoPedagogico.setUniversidade(optionalUniversidade.get());
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem", "Universidade não encontrada");
            }
            this.projetoPedagogicoRepository.save(projetoPedagogico);

            return new ModelAndView("redirect:/cursos/" + cursoId).addObject("sucesso", "Projeto pedagógico cadastrado com sucesso");
        }
    }

    @GetMapping("/projeto-pedagogico/{projetoPedagogicoId}/editar")
    public ModelAndView editar(@PathVariable Long projetoPedagogicoId, RequisicaoFormProjetoPedagogico requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para editar um projeto pedagógico");
        } else {
            Optional<ProjetoPedagogico> optionalpp = this.projetoPedagogicoRepository.findById(projetoPedagogicoId);
            ModelAndView mv = new ModelAndView("alteracao-projeto-pedagogico");
            if (optionalpp.isPresent()) {
                requisicao.setLink(optionalpp.get().getLink());
                mv.addObject("requisicaoFormProjetoPedagogico", requisicao);

                return mv;
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem", "Projeto pedagógico não encontrado");
            }

        }
    }

    @PostMapping("/projeto-pedagogico/{projetoPedagogicoId}/editar")
    public ModelAndView alteracaoProjetoPedagogico(@PathVariable Long projetoPedagogicoId, @Valid RequisicaoFormProjetoPedagogico requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para editar um projeto pedagógico");
        } else {
            if (binding.hasErrors()) {
                return new ModelAndView("alteracao-projeto-pedagogico");
            } else {
                Optional<ProjetoPedagogico> optionalProjetoPedagogico = this.projetoPedagogicoRepository.findById(projetoPedagogicoId);
                if (optionalProjetoPedagogico.isPresent()) {
                    ProjetoPedagogico projetoPedagogico = optionalProjetoPedagogico.get();
                    projetoPedagogico.setLink(requisicao.getLink());

                    this.projetoPedagogicoRepository.save(projetoPedagogico);

                    return new ModelAndView("redirect:/cursos/" + projetoPedagogico.getCurso().getId()).addObject("sucesso", "Projeto pedagógico editado com sucesso");
                } else {
                    return new ModelAndView("redirect:/cursos").addObject("mensagem", "Projeto Pedagógico não encontrado");
                }
            }
        }
    }

    @GetMapping("/projeto-pedagogico/{id}/excluir")
    public ModelAndView excluir(@PathVariable Long id, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para excluir um projeto pedagógico");
        } else {
            Optional<ProjetoPedagogico> optionalProjetoPedagogico = this.projetoPedagogicoRepository.findById(id);
            if (optionalProjetoPedagogico.isPresent()) {
                ModelAndView mv = new ModelAndView("redirect:/cursos/" + optionalProjetoPedagogico.get().getCurso().getId());
                mv.addObject("sucesso", "Projeto Pedagógico excluído com sucesso");

                this.projetoPedagogicoRepository.delete(optionalProjetoPedagogico.get());

                return mv;
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem", "Projeto pedagógico não encontrado");
            }
        }
    }

}
