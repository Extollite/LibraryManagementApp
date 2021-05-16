package pl.rjsk.librarymanagement.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rjsk.librarymanagement.model.entity.User;
import pl.rjsk.librarymanagement.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;

    @ModelAttribute("module")
    private String module() {
        return "users";
    }

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> users = userService.getAllUsers(PageRequest.of(page - 1, size));

        model.addAttribute("users", users);

        if (users.hasContent()) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, users.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "users";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam long userId) {
        userService.delete(userId);

        return "redirect:/users";
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        User user = new User();
        user.setPassword(UUID.randomUUID().toString());

        model.addAttribute("user", user);

        return "userAdd";
    }

    @PostMapping("/add/save")
    public String addUser(@ModelAttribute(value = "user") User user) {
        userService.save(user);

        return "redirect:/users";
    }
}
