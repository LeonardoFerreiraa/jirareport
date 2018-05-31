package br.com.leonardoferreira.jirareport.controller;

import java.util.List;
import java.util.Set;

import br.com.leonardoferreira.jirareport.domain.Board;
import br.com.leonardoferreira.jirareport.domain.form.BoardForm;
import br.com.leonardoferreira.jirareport.domain.vo.JiraProject;
import br.com.leonardoferreira.jirareport.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lferreira
 * @since 11/14/17 3:48 PM
 */
@Controller
@RequestMapping("/boards")
public class BoardController extends AbstractController {

    @Autowired
    private BoardService boardService;

    @GetMapping
    public ModelAndView index(final Pageable pageable, final Board board) {
        Page<Board> boards = boardService.findAll(pageable, board);

        return new ModelAndView("boards/index")
                .addObject("boards", boards)
                .addObject("board", board);
    }

    @GetMapping("/new")
    public ModelAndView create() {
        List<JiraProject> projects = boardService.findAllInJira();

        return new ModelAndView("boards/new")
                .addObject("projects", projects);
    }

    @PostMapping
    public ModelAndView create(final Board board) {
        boardService.create(board);

        return new ModelAndView(String.format("redirect:/boards/%s/edit", board.getId()));
    }

    @DeleteMapping("/{id}")
    public ModelAndView delete(@PathVariable final Long id) {
        boardService.delete(id);

        return new ModelAndView("redirect:/boards");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView update(@PathVariable final Long id) {
        BoardForm boardForm = boardService.findToUpdate(id);
        Set<String> statusFromProjectInJira = boardService.findStatusFromBoardInJira(id);

        return new ModelAndView("boards/edit")
                .addObject("boardForm", boardForm)
                .addObject("suggestedStatus", statusFromProjectInJira);
    }

    @PutMapping
    public ModelAndView update(final BoardForm board) {
        boardService.update(board);

        return new ModelAndView(String.format("redirect:/boards/%s/issue-periods", board.getId()));
    }
}
