package pl.rjsk.librarymanagement.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rjsk.librarymanagement.mapper.UserMapper;
import pl.rjsk.librarymanagement.model.dto.UserDto;
import pl.rjsk.librarymanagement.service.UserService;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public UserDto loginUser(@RequestParam String pesel) {
        return userMapper.mapToDto(userService.getUserByPesel(pesel));
    }

    @PatchMapping("/changePassword")
    public UserDto loginUser(@RequestParam String pesel, @RequestParam String newPassword) {
        return userMapper.mapToDto(userService.updatePassword(pesel, newPassword));
    }
}
