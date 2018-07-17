package springfiles.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import springfiles.dao.UsersStorage;
import springfiles.models.User;

@Service
public class UsersStorageImpl implements UsersStorage {

	public static final String USERS_DIRECTORY = "filestorage/";
	private Gson gson = new Gson();

	@Override
	public void saveUsers(List<User> users, String fileName) {
		String lines = users.stream().map(user -> gson.toJson(user)).collect(Collectors.joining("\n"));
		try {
			Files.write(Paths.get(USERS_DIRECTORY + fileName), lines.getBytes(StandardCharsets.UTF_8),
					getFileOption(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private OpenOption getFileOption(String fileName) {
		return Files.exists(Paths.get(USERS_DIRECTORY + fileName)) ? StandardOpenOption.APPEND
				: StandardOpenOption.CREATE;
	}

	@Override
	public void deleteUser(String id, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			List<User> userList = stream.map(line -> gson.fromJson(line, User.class))
					.filter(userFromList -> !userFromList.getId().equals(id)).collect(Collectors.toList());
			String lines = userList.stream().map(userFromList -> gson.toJson(userFromList))
					.collect(Collectors.joining("\n"));
			deleteAllFrom(fileName);
			Files.write(Paths.get(USERS_DIRECTORY + fileName), lines.getBytes(StandardCharsets.UTF_8),
					getFileOption(fileName));
		} catch (IOException e) {
			throw new RuntimeException();
		}

	}

	@Override
	public void deleteAllFrom(String fileName) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(USERS_DIRECTORY + fileName, false);
			PrintWriter printWriter = new PrintWriter(fileWriter, false);
			printWriter.flush();
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<User> readAllFrom(String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			return stream.map(line -> gson.fromJson(line, User.class)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public Optional<User> readById(String id, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			return stream.map(line -> gson.fromJson(line, User.class)).filter(u -> u.getId().equals(id)).findAny();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void updateUser(User user, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			List<User> userList = new ArrayList<User>();
			stream.map(line -> gson.fromJson(line, User.class)).forEach(eachUser -> {
				if (!eachUser.getId().equals(user.getId())) {
					System.out.println(user.getId()+" "+eachUser.getId());
					userList.add(eachUser);
				} else {
					userList.add(user);
				}
			});
			deleteAllFrom(fileName);
			saveUsers(userList, fileName);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

}
