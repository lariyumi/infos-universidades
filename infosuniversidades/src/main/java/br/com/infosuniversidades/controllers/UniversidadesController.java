package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.models.Universidade;
import br.com.infosuniversidades.repositories.UniversidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UniversidadesController {

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");

        return mv;
    }

    @GetMapping("/universidades")
    public ModelAndView universidades() {
        List<Universidade> universidades = this.universidadeRepository.findAll(); // retorna lista com todas as universidades presentes no banco de dados
        ModelAndView mv = new ModelAndView("universidades");
        mv.addObject("universidades", universidades);

        return mv;
    }

}