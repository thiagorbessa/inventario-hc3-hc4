package com.inventario.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public String handleRegraNegocio(RegraNegocioException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        return "redirect:/usuarios"; // Redireciona de volta com a mensagem de erro
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ModelAndView handleNotFound(EntidadeNaoEncontradaException ex) {
        ModelAndView mv = new ModelAndView("error/404"); // Crie uma página 404.html em templates/error
        mv.addObject("mensagem", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        ModelAndView mv = new ModelAndView("error/403"); // Crie uma página 403.html em templates/error
        mv.addObject("mensagem", ex.getMessage());
        return mv;
    }
}