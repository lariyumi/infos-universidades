package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormCurso;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
        List<Endereco> enderecos = this.enderecoRepository.findAll();
        mv.addObject("enderecos", enderecos);
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
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
        }
        if (binding.hasErrors()) {
            return new ModelAndView("redirect:/universidade/cadastro").addObject("mensagem", "Preencha todos os campos devidamente");
        } else {
            Universidade universidade = new Universidade();
            universidade.setNome(requisicao.getNome());
            universidade.setSite(requisicao.getSite());
            universidade.setTipo(requisicao.getTipoUniversidade());
            if (this.universidadeRepository.findByNome(universidade.getNome()).isPresent()) {
                return new ModelAndView("redirect:/universidade/cadastro").addObject("mensagem", "Já existe uma universidade com o nome fornecido");
            } else {
                List<Endereco> enderecos = requisicao.getEnderecos();
                if (enderecos.isEmpty()) {
                    return new ModelAndView("redirect:/universidade/cadastro").addObject("mensagem", "Preencha todos os campos devidamente");
                } else {
                    boolean camposValidos = true;
                    for (int i = 0; i < enderecos.size(); i++) {
                        if (enderecos.get(i).getCidade().isEmpty() || enderecos.get(i).getBairro().isEmpty() || enderecos.get(i).getRua().isEmpty()) {
                            camposValidos = false;
                        }
                    }
                    if (camposValidos == true) {
                        this.universidadeRepository.save(universidade);
                        for (int i = 0; i < enderecos.size(); i++) {
                            enderecos.get(i).setUniversidade(universidade);
                            this.enderecoRepository.save(enderecos.get(i));
                        }
                        ModelAndView mv = new ModelAndView("redirect:/");
                        mv.addObject("sucesso", "Nova universidade cadastrada com sucesso");

                        return mv;
                    } else {
                        return new ModelAndView("redirect:/universidade/cadastro").addObject("mensagem", "Preencha todos os campos devidamente");
                    }
                }
            }
        }
    }

    @GetMapping("/{id}")
    public ModelAndView visualizar(@PathVariable Long id, HttpServletRequest req) {
        Optional<Universidade> optional = this.universidadeRepository.findById(id);

        if (optional.isPresent()) {
            Universidade universidade = optional.get();
            List<Endereco> todosEnderecos = this.enderecoRepository.findAll();
            List<Endereco> enderecos = new ArrayList<>();
            for (Endereco endereco : todosEnderecos) {
                if (universidade.getId().equals(endereco.getUniversidade().getId())) {
                    enderecos.add(endereco);
                }
            }

            ModelAndView mv = new ModelAndView("universidade");
            mv.addObject("universidade", universidade);
            mv.addObject("enderecos", enderecos);

            if (req.getSession(false) != null) {
                Optional<Usuario> optionalUsuario = this.usuarioRepository.findByEmail(req.getSession().getAttribute("email").toString());
                if (optionalUsuario.isPresent()) {
                    Usuario usuario = optionalUsuario.get();
                    mv.addObject("usuario", usuario);
                }
            }
            return mv;
        } else {
            return new ModelAndView("redirect:/").addObject("mensagem", "A Universidade selecionada não foi encontrada no banco de dados");
        }
    }

    @GetMapping("/{id}/editar")
    public ModelAndView edicao(@PathVariable Long id, RequisicaoFormUniversidade requisicao, HttpServletRequest req) {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("role").equals("ADM")) {
            ModelAndView mv = new ModelAndView("alteracao-universidade");
            Optional<Universidade> optional = this.universidadeRepository.findById(id);
            if (optional.isPresent()) {
                Universidade universidade = optional.get();
                mv.addObject("listaTipoUniversidade", TipoUniversidade.values());

                List<Endereco> todosEnderecos = this.enderecoRepository.findAll();
                List<Endereco> enderecos = new ArrayList<>();
                for (Endereco endereco : todosEnderecos) {
                    if (universidade.getId().equals(endereco.getUniversidade().getId())) {
                        enderecos.add(endereco);
                    }
                }

                requisicao.fromUniversidade(universidade, enderecos);

                mv.addObject("requisicaoFormUniversidade", requisicao);

                return mv;
            } else {
                return new ModelAndView("redirect:/").addObject("mensagem", "A universidade procurada não foi encontrada");
            }
        } else {
            return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
        }
    }

    @PostMapping("/{id}/editar")
    public ModelAndView alterar(@PathVariable Long id, @ModelAttribute RequisicaoFormUniversidade requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
        }
        if (binding.hasErrors()) {
            ModelAndView mv = new ModelAndView("alteracao-universidade");
            mv.addObject("listaTipoUniversidade", TipoUniversidade.values());
            mv.addObject("id", id);

            return mv;
        } else {
            Optional<Universidade> optional = this.universidadeRepository.findById(id);
            if (optional.isPresent()) {
                boolean camposValidos = true;
                for (int i = 0; i < requisicao.getEnderecos().size(); i++) {
                    if (requisicao.getEnderecos().get(i).getCidade().isEmpty() || requisicao.getEnderecos().get(i).getBairro().isEmpty() || requisicao.getEnderecos().get(i).getRua().isEmpty()) {
                        camposValidos = false;
                        requisicao.getEnderecos().get(i).setUniversidade(optional.get());
                    }
                }
                if (camposValidos) {
                    System.out.println(requisicao.getEnderecos().get(1).getBairro());
                    Universidade universidade = requisicao.toUniversidade(optional.get(), requisicao.getEnderecos());
                    this.universidadeRepository.save(universidade);
                    System.out.println(universidade.getEnderecos().get(0).getBairro());
                    for (Endereco endereco : universidade.getEnderecos()) {
                        System.out.println(endereco.getBairro());
                        this.enderecoRepository.save(endereco);
                    }
                }

                return new ModelAndView("redirect:/" + id).addObject("mensagem", "Universidade atualizada com sucesso");
            } else {
                return new ModelAndView("redirect:/").addObject("mensagem", "Universidade não encontrada");
            }
        }
    }

    @GetMapping("/universidade/cadastro/curso")
    public ModelAndView cadastroCurso(RequisicaoFormCurso requisicao, HttpServletRequest req) {
        return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
    }

}