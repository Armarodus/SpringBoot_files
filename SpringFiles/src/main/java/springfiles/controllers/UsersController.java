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
import org.springframework.web.bind.annotation.RestController;

import springfiles.models.User;
import springfiles.services.UsersStorageImpl;

@RestController
public class UsersController {

	@Autowired
	private UsersStorageImpl userStorageImpl;

	@GetMapping(value = "read/{fileName}")
	public List<User> readAllFrom(@PathVariable String fileName) {
		return userStorageImpl.readAllFrom(fileName);
	}

	@PostMapping(value = "save/{fileName}")
	public void saveUsers(@PathVariable String fileName, @RequestBody List<User> users) {
		userStorageImpl.saveUsers(users, fileName);
	}

	@DeleteMapping(value = "/{fileName}/all")
	public void deleteAllFromUsers(@PathVariable String fileName) {
		userStorageImpl.deleteAllFrom(fileName);
	}

	@DeleteMapping(value = "/{fileName}/{id}")
	public void deleteUser(@PathVariable String fileName, @PathVariable String id) {
		userStorageImpl.deleteUser(id, fileName);
	}

	@GetMapping(value = "/read/{fileName}/{id}")
	public Optional<User> readUser(@PathVariable String fileName, @PathVariable String id) {
		return userStorageImpl.readById(id, fileName);
	}

	@PutMapping(value = "/update/{fileName}")
	public void updateUser(@PathVariable String fileName,@RequestBody User user) {
		userStorageImpl.updateUser(user, fileName);
	}

}
