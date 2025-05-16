package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormUniversidade;
import br.com.infosuniversidades.models.*;
import br.com.infosuniversidades.repositories.*;
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

import static org.apache.logging.log4j.util.Strings.isBlank;

@Controller
public class UniversidadeController {

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ProjetoPedagogicoRepository projetoPedagogicoRepository;

    @GetMapping("")
    public ModelAndView index(HttpServletRequest req) {
        List<Universidade> universidades = this.universidadeRepository.findAll();// retorna lista com todas as universidades presentes no banco de dados
        Collections.sort(universidades, Comparator.comparing(Universidade::getNome));
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
                mv.addObject("id", universidade.getId());

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
            List<Endereco> enderecos = new ArrayList<>(requisicao.getEnderecos());
            enderecos.removeIf(e -> e == null || (isBlank(e.getCidade()) && isBlank(e.getBairro()) && isBlank(e.getRua())));
            if (enderecos.isEmpty()) {
                return new ModelAndView("alteracao-universidade").addObject("mensagem", "Por favor, coloque pelo menos um endereço").addObject("listaTipoUniversidade", TipoUniversidade.values());
            }
            if (optional.isPresent()) {
                boolean camposValidos = true;
                Universidade universidade = requisicao.toUniversidade(optional.get(), requisicao.getEnderecos());
                for (int i = 0; i < enderecos.size(); i++) {
                    enderecos.get(i).setUniversidade(universidade);
                    if (enderecos.get(i).getCidade().isEmpty() || enderecos.get(i).getBairro().isEmpty() || enderecos.get(i).getRua().isEmpty()) {
                        camposValidos = false;
                    }
                }
                if (camposValidos) {
                    this.universidadeRepository.save(universidade);
                    List<Endereco> todosEnderecos = this.enderecoRepository.findAll();
                    List<Endereco> deletar = new ArrayList<>();
                    int cont = 0;
                    for (int i = 0; i < todosEnderecos.size(); i++) {
                        if (todosEnderecos.get(i).getUniversidade().getId().equals(universidade.getId())) {
                            if (enderecos.size() > i) {
                                enderecos.get(cont).setId(todosEnderecos.get(i).getId());
                                cont++;
                            } else {
                                deletar.add(todosEnderecos.get(i));
                            }
                        }
                    }
                    for (Endereco endereco : enderecos) {
                        this.enderecoRepository.save(endereco);
                    }

                    for (Endereco endereco : deletar) {
                        this.enderecoRepository.delete(endereco);
                    }

                    return new ModelAndView("redirect:/" + id).addObject("mensagem", "Universidade atualizada com sucesso");
                } else {
                    return new ModelAndView("alteracao-universidade").addObject("mensagem", "Preeencha todos os campos antes de prosseguir").addObject("listaTipoUniversidade", TipoUniversidade.values());
                }
            } else {
                return new ModelAndView("redirect:/").addObject("mensagem", "Universidade não encontrada");
            }
        }
    }

    @GetMapping("/{id}/excluir")
    public ModelAndView excluir(@PathVariable Long id, HttpServletRequest request) {
        if (request.getSession(false) == null || !request.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para realizar essa ação");
        } else {
            Optional<Universidade> optional = this.universidadeRepository.findById(id);
            if (optional.isPresent()) {
                Universidade universidade = optional.get();
                List<Endereco> enderecos = universidade.getEnderecos();
                List<ProjetoPedagogico> projetosPedagogicos = universidade.getProjetosPedagogicos();

                for (Endereco endereco : enderecos) {
                    this.enderecoRepository.delete(endereco);
                }

                for (ProjetoPedagogico projetoPedagogico : projetosPedagogicos) {
                    this.projetoPedagogicoRepository.delete(projetoPedagogico);
                }

                universidadeRepository.delete(universidade);

                return new ModelAndView("redirect:/").addObject("sucesso", "Universidade foi excluida com sucesso");
            } else {
                return new ModelAndView("redirect:/").addObject("mensagem", "Universidade não encontrada");
            }
        }
    }

}