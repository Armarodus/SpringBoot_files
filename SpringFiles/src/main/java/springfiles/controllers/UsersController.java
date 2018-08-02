package springfiles.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfiles.models.User;
import springfiles.services.UsersStorageImpl;

@RestController
@RequestMapping("/file/{fileName}/users")
public class UsersController {

	@Autowired
	private UsersStorageImpl userStorageImpl;

	@GetMapping(value = "/get")
	public List<User> getUsers(@PathVariable String fileName, @RequestHeader(value = "offset") String offset,
			@RequestHeader(value = "pageSize") String pageSize) {
		return userStorageImpl.getUsers(offset, pageSize, fileName);
	}
	// offset and page size to header

	@PostMapping(value = "/save")
	public void saveUsers(@PathVariable String fileName, @RequestBody List<User> users) {
		userStorageImpl.save(users, fileName);
	}

	@DeleteMapping(value = "/all")
	public void deleteAllFromUsers(@PathVariable String fileName) {
		userStorageImpl.deleteAllFrom(fileName);
	}

	@DeleteMapping(value = "/{id}")
	public void deleteUser(@PathVariable String fileName, @PathVariable String id) {
		userStorageImpl.delete(id, fileName);
	}

	@GetMapping(value = "/{id}")
	public Optional<User> readUser(@PathVariable String fileName, @PathVariable String id) {
		return userStorageImpl.getById(id, fileName);
	}

	@PutMapping(value = "/{id}")
	public void updateUser(@PathVariable String fileName, @RequestBody User user) {
		userStorageImpl.update(user, fileName);
	}

}
