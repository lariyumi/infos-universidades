package br.com.infosuniversidades.controllers;

import br.com.infosuniversidades.dto.RequisicaoFormCurso;
import br.com.infosuniversidades.models.*;
import br.com.infosuniversidades.repositories.CursoRepository;
import br.com.infosuniversidades.repositories.UniversidadeRepository;
import br.com.infosuniversidades.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping(value = "/cursos")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UniversidadeRepository universidadeRepository;

    @GetMapping("")
    public ModelAndView cursos(HttpServletRequest req) {
        List<Curso> cursos = this.cursoRepository.findAll(); // retorna lista com todas as universidades presentes no banco de dados
        Collections.sort(cursos, Comparator.comparing(Curso::getNome));
        ModelAndView mv = new ModelAndView("cursos");
        mv.addObject("cursos", cursos);
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

    @GetMapping("/cadastro")
    public ModelAndView cadastro(RequisicaoFormCurso requisicao, HttpServletRequest req) {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("role").equals("ADM")) {
            List<Universidade> universidades = this.universidadeRepository.findAll();
            ModelAndView mv = new ModelAndView("cadastro-curso");
            mv.addObject("universidades", universidades);

            return mv;
        } else {
            return new ModelAndView("redirect:/").addObject("mensagem", "É necessário ter um cargo de administrador para cadastrar um novo curso");
        }
    }

    @PostMapping("/cadastro")
    public ModelAndView novoCurso(@Valid RequisicaoFormCurso requisicao, BindingResult binding, HttpServletRequest req) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
        }
        if (binding.hasErrors()) {
            return new ModelAndView("redirect:/cursos/cadastro").addObject("mensagem", "Preencha todos os campos devidamente");
        } else {
            Curso curso = new Curso();
            curso.setNome(requisicao.getNome());
            curso.setDescricao(requisicao.getDescricao());
            if (this.cursoRepository.findByNome(curso.getNome()).isPresent()) {
                return new ModelAndView("redirect:/cursos/cadastro").addObject("mensagem", "O curso que você deseja cadastrar já existe");
            } else {
                List<Universidade> universidades = requisicao.getUniversidades();
                List<Curso> cursos = new ArrayList<>();
                cursos.add(curso);
                for (Universidade universidade : universidades) {
                    universidade.setCursos(cursos);
                }
                curso.setUniversidade(universidades);
                this.cursoRepository.save(curso);
                return new ModelAndView("redirect:/cursos").addObject("sucesso", "Curso cadastrado com sucesso");
            }
        }
    }

    @GetMapping("/{id}")
    public ModelAndView visualizarCurso(@PathVariable Long id, HttpServletRequest req) {
        ModelAndView mv = new ModelAndView("visualizacao-curso");
        Optional<Curso> curso = this.cursoRepository.findById(id);
        if (curso.isPresent()) {
            mv.addObject("curso", curso.get());

            List<Universidade> universidades = curso.get().getUniversidade();
            mv.addObject("universidades", universidades);

            List<ProjetoPedagogico> projetosPedagogicos = curso.get().getProjetosPedagogicos();
            mv.addObject("projetosPedagogicos", projetosPedagogicos);

            Map<Long, Boolean> temProjeto = new HashMap<>();
            for (Universidade universidade : universidades) {
                if (projetosPedagogicos.stream().anyMatch(pp -> pp.getUniversidade().getId().equals(universidade.getId()))) {
                    temProjeto.put(universidade.getId(), true);
                } else {
                    temProjeto.put(universidade.getId(), false);
                }
            }
            mv.addObject("temProjeto", temProjeto);
        } else {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "Curso não encontrado");
        }
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

    @GetMapping("/{id}/editar")
    public ModelAndView editarCurso(@PathVariable Long id, RequisicaoFormCurso requisicao,HttpServletRequest req) {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("role").equals("ADM")) {
            List<Universidade> universidades = this.universidadeRepository.findAll();
            Optional<Curso> optional = this.cursoRepository.findById(id);
            if(optional.isPresent()) {
                ModelAndView mv = new ModelAndView("alteracao-curso");
                requisicao.setNome(optional.get().getNome());
                requisicao.setDescricao(optional.get().getDescricao());
                requisicao.setUniversidades(optional.get().getUniversidade());
                mv.addObject("requisicaoFormCurso", requisicao);
                mv.addObject("universidades", universidades);

                return mv;
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem","Curso não encontrado");
            }

        } else {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para cadastrar um novo curso");
        }
    }

    @PostMapping("/{id}/editar")
    public ModelAndView novoCurso(@Valid RequisicaoFormCurso requisicao, BindingResult binding, HttpServletRequest req, @PathVariable Long id) {
        if (req.getSession(false) == null || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para acessar essa página");
        }
        if (binding.hasErrors()) {
            return new ModelAndView("redirect:/cursos/" + id + "/editar").addObject("mensagem", "Preencha todos os campos devidamente");
        } else {
            if (this.cursoRepository.findByNome(requisicao.getNome()).isPresent() &&  this.cursoRepository.findByNome(requisicao.getNome()).get().getId() != id) {
                return new ModelAndView("redirect:/cursos/cadastro").addObject("mensagem", "O curso que você deseja cadastrar já existe");
            } else {
                Optional<Curso> optionalCurso = this.cursoRepository.findById(id);
                if (optionalCurso.isPresent()) {
                    Curso curso = optionalCurso.get();
                    curso.setNome(requisicao.getNome());
                    curso.setDescricao(requisicao.getDescricao());
                    curso.setUniversidade(new ArrayList<>());
                    List<Curso> cursos = new ArrayList<>();
                    List<Universidade> universidades = requisicao.getUniversidades();
                    List<Universidade> todasUniversidades = this.universidadeRepository.findAll();

                    for (Universidade universidade : todasUniversidades) {
                        if (!universidades.contains(universidade) && universidade.getCursos().contains(curso)) {
                            universidade.getCursos().remove(curso);
                        }
                    }

                    for (Universidade universidade : universidades) {
                        cursos.addAll(universidade.getCursos());
                        if (!universidade.getCursos().contains(curso)) {
                            cursos.add(curso);
                        }
                        universidade.setCursos(cursos);
                        cursos = new ArrayList<>();
                    }

                    curso.setUniversidade(universidades);
                    this.cursoRepository.save(curso);

                    return new ModelAndView("redirect:/cursos/" + id).addObject("sucesso", "Curso alterado com sucesso");
                } else {
                    return new ModelAndView("redirect:/cursos").addObject("mensagem", "Curso não encontrado");
                }
            }
        }
    }

    @GetMapping("/{id}/excluir")
    public ModelAndView excluir(@PathVariable Long id, HttpServletRequest req) {
        if (req.getSession(false) == null  || !req.getSession(false).getAttribute("role").equals("ADM")) {
            return new ModelAndView("redirect:/cursos").addObject("mensagem", "É necessário ter um cargo de administrador para excluir um curso");
        } else {
            Optional<Curso> optional = this.cursoRepository.findById(id);
            if (optional.isPresent()) {
                Curso curso = optional.get();
                List<Universidade> universidades = curso.getUniversidade();
                List<ProjetoPedagogico> projetosPedagogicos = curso.getProjetosPedagogicos();

                for (Universidade universidade : universidades) {
                    universidade.getCursos().remove(curso);
                    this.universidadeRepository.save(universidade);
                }

                this.cursoRepository.delete(curso);

                return new ModelAndView("redirect:/cursos").addObject("sucesso", "Universidade excluída com sucesso");
            } else {
                return new ModelAndView("redirect:/cursos").addObject("mensagem", "Universidade não foi encontrada");
            }
        }
    }

}
