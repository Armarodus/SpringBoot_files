package springfiles.dao;

import java.util.List;
import java.util.Optional;

import springfiles.models.User;

public interface UsersStorage {

	public void save(List<User> users, String fileName);

	public void delete(String id, String fileName);

	public void deleteAllFrom(String fileName);

	public List<User> getUsers(String offset, String pageSize, String fileName);

	public Optional<User> getById(String id, String fileName);

	public void update(User user, String fileName);
}
