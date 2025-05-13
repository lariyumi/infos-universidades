package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormUniversidade;
import br.com.infosuniversidades.models.Endereco;
import br.com.infosuniversidades.models.TipoUniversidade;
import br.com.infosuniversidades.models.Universidade;
import br.com.infosuniversidades.models.Usuario;
import br.com.infosuniversidades.repositories.EnderecoRepository;
import br.com.infosuniversidades.repositories.UniversidadeRepository;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class UniversidadesController {

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @GetMapping("")
    public ModelAndView index(HttpServletRequest req) {
        List<Universidade> universidades = this.universidadeRepository.findAll(); // retorna lista com todas as universidades presentes no banco de dados
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("universidades", universidades);
        if (req.getSession(false) != null) {
            Optional<Usuario> optional = this.usuarioRepository.findByEmail(req.getSession().getAttribute("email").toString());
            if(optional.isPresent()) {
                Usuario usuario = new Usuario();
                usuario.setTipo(optional.get().getTipo());

                mv.addObject(usuario);
            }
        }

        return mv;
    }

    @GetMapping("/universidade/cadastro")
    public ModelAndView cadastroUniversidade(RequisicaoFormUniversidade requisicao, HttpServletRequest req) {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("role").equals("ADM")) {
            ModelAndView mv = new ModelAndView("cadastro-universidade");
            mv.addObject("listaTipoUniversidade", TipoUniversidade.values());

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "É necessário ter um cargo de administrador para conseguir adicionar uma nova universidade");

            return mv;
        }
    }

    @PostMapping("/universidade/cadastro")
    public ModelAndView novaUniversidade(@Valid RequisicaoFormUniversidade requisicao, BindingResult binding, HttpServletRequest req) {
        if (binding.hasErrors()) {
            System.out.println("************" + binding.getAllErrors());
            ModelAndView mv = new ModelAndView("cadastro-universidade");
            mv.addObject("listaTipoUniversidade", TipoUniversidade.values());

            return mv;
        } else {
            System.out.println("**********Entrou aqui - Nova Universidade");
            Universidade universidade = new Universidade();
            universidade.setNome(requisicao.getNome());
            universidade.setSite(requisicao.getSite());
            universidade.setTipo(requisicao.getTipoUniversidade());
            if (this.universidadeRepository.findByNome(universidade.getNome()).isPresent()) {
                System.out.println("**********Entrou aqui - Já existe");
                ModelAndView mv = new ModelAndView("cadastro-universidade");
                mv.addObject("mensagem", "A universidade que deseja cadastrar já existe");
                mv.addObject("listaTipoUniversidade", TipoUniversidade.values());

                return mv;
            } else {
                System.out.println("**********Entrou aqui - Salvar universidade");
                List<Endereco> enderecos = requisicao.getEnderecos();
                this.universidadeRepository.save(universidade);
                for (int i = 0; i < enderecos.size(); i++) {
                    if (enderecos.get(i).getBairro() != null && enderecos.get(i).getCidade() != null && enderecos.get(i).getRua() != null) {
                        enderecos.get(i).setUniversidade(universidade);
                        this.enderecoRepository.save(enderecos.get(i));
                    }
                }
                ModelAndView mv = new ModelAndView("redirect:/");
                mv.addObject("mensagem", "Nova universidade cadastrada com sucesso");

                return mv;
            }
        }
    }

}