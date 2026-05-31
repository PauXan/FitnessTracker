package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserSimpleDto;
import pl.wsb.fitnesstracker.user.api.UserEmailDto;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for user management operations (CRUD).
 * All endpoints are available under /v1/users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    /**
     * Returns all users with full details.
     *
     * @return list of all users as DTOs
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Returns details of a single user by ID.
     *
     * @param id user ID from the URL path
     * @return full user details or 404 if not found
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Returns all users with only ID and name (no birthdate or email).
     *
     * @return simplified list of all users
     */
    @GetMapping("/simple")
    public List<UserSimpleDto> getAllSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Returns all users born before the given date (older than that date).
     *
     * @param time cutoff date in path (yyyy-MM-dd)
     * @return list of matching users with full details
     */
    @GetMapping("/older/{time}")
    public List<UserDto> getUsersOlderThan(@PathVariable LocalDate time) {
        return userService.findUsersOlderThan(time)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Searches users by email fragment, case-insensitively.
     *
     * @param email fragment to search for (query parameter)
     * @return list of matching users with only ID and email
     */
    @GetMapping("/email")
    public List<UserEmailDto> getUsersByEmail(@RequestParam String email) {
        return userService.findUsersByEmailFragment(email)
                .stream()
                .map(userMapper::toEmailDto)
                .toList();
    }

    /**
     * Updates an existing user by ID.
     *
     * @param userId  user ID from the URL path
     * @param userDto new user data from request body
     * @return updated user as DTO
     */
    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userMapper.toDto(
                userService.updateUser(userId, userMapper.toEntity(userDto))
        );
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId user ID from the URL path
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    /**
     * Creates a new user.
     *
     * @param userDto user data from the request body
     * @return created user as DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userMapper.toDto(
                userService.createUser(userMapper.toEntity(userDto))
        );
    }

}