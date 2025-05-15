package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoPerfilUsuario;
import br.com.infosuniversidades.dto.RequisicaoFormUsuario;
import br.com.infosuniversidades.dto.RequisicaoLoginUsuario;
import br.com.infosuniversidades.models.Usuario;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cadastro")
    public ModelAndView cadastro(RequisicaoFormUsuario requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("cadastro");

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar logar/cadastrar uma nova conta");

            return mv;
        }
    }

    @PostMapping("/cadastro")
    public ModelAndView criar(@Valid RequisicaoFormUsuario requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) != null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar cadastrar uma nova conta");

            return mv;
        }
        if (binding.hasErrors()) {
            return new ModelAndView("cadastro");
        } else {
            //cria um usuário
            Usuario usuario = requisicao.toUsuario();
            if (this.usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                ModelAndView mv = new ModelAndView("cadastro");
                mv.addObject("mensagem", "O e-mail já foi utilizado para outro usuário");

                return mv;
            } else {
                //Salva o usuário no banco de dados
                this.usuarioRepository.save(usuario);

                HttpSession session = req.getSession(true);
                session.setAttribute("email", usuario.getEmail());
                session.setAttribute("nome", usuario.getNome());
                session.setAttribute("role", usuario.getTipo().toString());

                return new ModelAndView("redirect:/");
            }
        }
    }

    @GetMapping("/login")
    public ModelAndView login(RequisicaoLoginUsuario requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("login");

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar logar/cadastrar uma nova conta");

            return mv;
        }
    }

    @PostMapping("/login")
    public ModelAndView logar(@Valid RequisicaoLoginUsuario requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) != null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar se logar em outra conta");

            return mv;
        }
        if (binding.hasErrors()) {
            return new ModelAndView("login");
        } else {
            //cria um usuário
            Optional<Usuario> usuarioLogado = this.usuarioRepository.findByEmail(requisicao.getEmail());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);

            if (usuarioLogado.isPresent() && passwordEncoder.matches(requisicao.getSenha(), usuarioLogado.get().getSenha())) {
                HttpSession session = req.getSession(true);
                Usuario usuario = requisicao.toUsuario(usuarioLogado.get());
                session.setAttribute("email", usuarioLogado.get().getEmail());
                session.setAttribute("nome", usuarioLogado.get().getNome());
                session.setAttribute("role", usuarioLogado.get().getTipo().toString());

                return new ModelAndView("redirect:/");
            } else {
                ModelAndView mv = new ModelAndView("login");
                mv.addObject("mensagem", "E-mail ou senha inválidos");

                return mv;
            }
        }
    }

    @GetMapping("/login/alteracao-senha")
    public ModelAndView alterarSenha(RequisicaoLoginUsuario requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("alteracao-senha");

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar logar/cadastrar uma nova conta");

            return mv;
        }
    }

    @PostMapping("/login/alteracao-senha")
    public ModelAndView mudarSenha(@Valid RequisicaoLoginUsuario requisicao, BindingResult binding) {
        if (binding.hasErrors()) {
            return new ModelAndView("alteracao-senha");
        } else {
            Optional<Usuario> usuarioEncontrado = this.usuarioRepository.findByEmail(requisicao.getEmail());
            if (usuarioEncontrado.isEmpty()) {
                ModelAndView mv = new ModelAndView("alteracao-senha");
                mv.addObject("mensagem", "E-mail fornecido inválido");

                return mv;
            } else {
                this.usuarioRepository.save(usuarioEncontrado.get());
                ModelAndView mv = new ModelAndView("redirect:/login");
                mv.addObject("mensagem", "Senha alterada com sucesso");

                return mv;
            }
        }
    }

    @GetMapping("/perfil")
    public ModelAndView perfil(HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Entre na sua conta antes de acessar seu perfil");

            return mv;
        } else {
            return new ModelAndView("perfil");
        }
    }

    @GetMapping("/perfil/alterar-perfil")
    public ModelAndView mostrarDados(@Valid RequisicaoFormUsuario requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Entre na sua conta antes de acessar seu perfil");

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("alteracao-perfil");
            Optional<Usuario> optional = this.usuarioRepository.findByEmail(req.getSession().getAttribute("email").toString());
            if (optional.isPresent()){
                Usuario usuario = optional.get();
                mv.addObject("requisicaoPerfilUsuario", usuario);

                return mv;
            } else {
                ModelAndView mv2 = new ModelAndView("redirect:/perfil");
                mv2.addObject("mensagem", "Usuário não encontrado");

                return mv2;
            }
        }
    }

    @PostMapping("/perfil/alterar-perfil")
    public ModelAndView editar(@Valid RequisicaoPerfilUsuario requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Entre na sua conta antes de acessar seu perfil");

            return mv;
        }
        if (binding.hasErrors()) {
            return new ModelAndView("alteracao-perfil");
        }
        else {
            if (this.usuarioRepository.findByEmail(requisicao.getEmail()).isEmpty() || this.usuarioRepository.findByEmail(requisicao.getEmail()).get().getEmail().equals(req.getSession().getAttribute("email").toString())) {
                Usuario usuario = new Usuario();
                usuario.setNome(requisicao.getNome());
                usuario.setEmail(requisicao.getEmail());
                Optional<Usuario> optional = this.usuarioRepository.findByEmail(req.getSession().getAttribute("email").toString());
                if (optional.isPresent()) {
                    usuario.setId(optional.get().getId());
                    usuario.setTipo(optional.get().getTipo());
                    usuario.setSenha(optional.get().getSenha());
                    this.usuarioRepository.save(usuario);

                    req.getSession().setAttribute("nome", usuario.getNome());
                    req.getSession().setAttribute("email", usuario.getEmail());

                    ModelAndView mv = new ModelAndView("redirect:/perfil");
                    mv.addObject("sucesso", "Perfil alterado com sucesso!");

                    return mv;
                } else {
                    ModelAndView mv = new ModelAndView("alteracao-perfil");
                    mv.addObject("mensagem", "Usuário a ser alterado não encontrado");

                    return mv;
                }
            } else {
                ModelAndView mv = new ModelAndView("alteracao-perfil");
                mv.addObject("mensagem", "Já existe um usuário com o e-mail fornecido");

                return mv;
            }
        }
    }

}
