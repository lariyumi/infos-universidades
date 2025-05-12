package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.models.Universidade;
import br.com.infosuniversidades.models.Usuario;
import br.com.infosuniversidades.repositories.UniversidadeRepository;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class UniversidadesController {

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

}