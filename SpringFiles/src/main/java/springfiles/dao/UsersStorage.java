package springfiles.dao;

import java.util.List;
import java.util.Optional;

import springfiles.models.User;

public interface UsersStorage {

	public void saveUsers(List<User> users, String fileName);

	public void deleteUser(String id, String fileName);

	public void deleteAllFrom(String fileName);

	public List<User> readAllFrom(String fileName);

	public Optional<User>readById(String id, String fileName);

	public void updateUser(User user, String fileName);
}
