package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormUsuario;
import br.com.infosuniversidades.dto.RequisicaoLoginUsuario;
import br.com.infosuniversidades.models.Usuario;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        if (binding.hasErrors()) {
            return new ModelAndView("login");
        } else {
            //cria um usuário
            Optional<Usuario> usuarioLogado = this.usuarioRepository.findByEmail(requisicao.getEmail());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);
            System.out.println("************************" + usuarioLogado.isPresent());
            System.out.println(requisicao.getSenha());
            System.out.println(usuarioLogado.get().getSenha());
            System.out.println(passwordEncoder.matches(requisicao.getSenha(), usuarioLogado.get().getSenha()));

            if (usuarioLogado.isPresent() && passwordEncoder.matches(requisicao.getSenha(), usuarioLogado.get().getSenha())) {
                System.out.println("***************Entrou aqui");
                HttpSession session = req.getSession(true);
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
    public ModelAndView alterarSenha(RequisicaoFormUsuario requisicao, HttpServletRequest req) {
        if (req.getSession(false) == null) {
            ModelAndView mv = new ModelAndView("alteracao-senha");

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("redirect:/");
            mv.addObject("mensagem", "Saia da sua conta antes de tentar logar/cadastrar uma nova conta");

            return mv;
        }
    }

}
