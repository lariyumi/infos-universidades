package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormUsuario;
import br.com.infosuniversidades.models.Usuario;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cadastro")
    public ModelAndView cadastro(RequisicaoFormUsuario requisicao) {
        ModelAndView mv = new ModelAndView("cadastro");

        return mv;
    }

    @PostMapping("/cadastro")
    public ModelAndView criar(@Valid RequisicaoFormUsuario requisicao, BindingResult binding, HttpServletRequest req) {
        if (binding.hasErrors()) {
            return new ModelAndView("/cadastro");
        } else {
            //cria um usuário
            Usuario usuario = requisicao.toUsuario();
            if (this.usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                ModelAndView mv = new ModelAndView("/cadastro");
                mv.addObject("mensagem", "O e-mail já foi utilizado para outro usuário");

                return mv;
            } else {
                if (req.getSession(false) == null) {
                    //Salva o usuário no banco de dados
                    this.usuarioRepository.save(usuario);

                    HttpSession session = req.getSession(true);
                    session.setAttribute("email", usuario.getEmail());
                    session.setAttribute("nome", usuario.getNome());
                    session.setAttribute("role", usuario.getTipo().toString());

                    return new ModelAndView("redirect:/");
                } else {
                    ModelAndView mv = new ModelAndView("redirect:/");
                    mv.addObject("mensagem", "Saia da sua conta antes de tentar logar/cadastrar uma nova conta");

                    return mv;
                }
            }
        }
    }

}
