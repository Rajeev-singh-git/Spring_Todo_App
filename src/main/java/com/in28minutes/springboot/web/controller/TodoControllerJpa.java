package com.in28minutes.springboot.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.in28minutes.springboot.web.model.Todo;
import com.in28minutes.springboot.web.service.TodoService;

@Controller
public class TodoControllerJpa {

	//@Autowired
	//TodoService service;
	
	@Autowired
	TodoRepository todoRepository;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}

	
	@RequestMapping(value = "/list-todos", method = RequestMethod.GET)
	public String showTodos(ModelMap model) {
		String USER = getLoggedInUserName(model);
		List<Todo> todos = todoRepository.findByUser(USER);
		model.addAttribute("todos", todos);
		return "list-todos";
	}


	//private String getLoggedInUserName(ModelMap model) {
	//	return (String) model.get("name");
	//}
	private String getLoggedInUserName(ModelMap model) {
		Object principal =
				SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	
		if(principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername(); //type_casting principal
		}
		return principal.toString();
	}
		

	@RequestMapping(value = "/add-todo", method = RequestMethod.GET)
	public String showAddTodoPage(ModelMap model) {
		model.addAttribute("todo", new Todo(0, getLoggedInUserName(model), "Default Desc",
				new Date(), false));
		return "todo";
	}

	@RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
	public String deleteTodo(@RequestParam int id) {
		
		todoRepository.deleteById(id);
		return "redirect:/list-todos";
	}

	@RequestMapping(value = "/add-todo", method = RequestMethod.POST)
	public String addTodo(ModelMap model, @Valid Todo todo, BindingResult result) {
		
		if(result.hasErrors()){
			return "todo";
		}
		
//		service.addTodo(getLoggedInUserName(model), todo.getDesc(), todo.getTargetDate(),
//				false);
		String USER = getLoggedInUserName(model);
		todo.setUser(USER);
		todoRepository.save(todo);
		return "redirect:/list-todos";
	}
	
	@RequestMapping(value = "/update-todo", method = RequestMethod.GET)
	public String showUpdateTodoPage(@RequestParam int id, ModelMap model) {
	//	Todo todo = service.retrieveTodo(id);
		Todo todo = todoRepository.findById(id).get();
		model.put("todo", todo);
	    return "todo";
	}
	
	@RequestMapping(value = "/update-todo", method = RequestMethod.POST)
	public String UpdateTodo( ModelMap model, @Valid Todo todo, BindingResult result) {
	  
			if(result.hasErrors()){
			return "todo";
		}
			
		todo.setUser(getLoggedInUserName(model));
		todoRepository.save(todo);
	//	service.updateTodo(todo);
		
	//	service.addTodo((String) model.get("name"), todo.getDesc(), new Date(),
	//			false);
		return "redirect:/list-todos";
	
	}
	
	
	
}