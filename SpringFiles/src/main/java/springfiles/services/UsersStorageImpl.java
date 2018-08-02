package springfiles.services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import springfiles.dao.UsersStorage;
import springfiles.models.User;

@Service
public class UsersStorageImpl implements UsersStorage {

	public static final String USERS_DIRECTORY = "filestorage/";
	private Gson gson = new Gson();
	private final Logger logger = LoggerFactory.getLogger(UsersStorageImpl.class);

	@Override
	public void save(List<User> users, String fileName) {
		String lines = users.stream().map(user -> gson.toJson(user)).collect(Collectors.joining("\n"));
		try {
			Files.write(Paths.get(USERS_DIRECTORY + fileName), lines.getBytes(StandardCharsets.UTF_8),
					getFileOption(fileName));
			logger.info("File: '{}' was updated!", fileName);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private OpenOption getFileOption(String fileName) {
		return Files.exists(Paths.get(USERS_DIRECTORY + fileName)) ? StandardOpenOption.APPEND
				: StandardOpenOption.CREATE;
	}

	@Override
	public void delete(String id, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			List<User> userList = stream.map(line -> gson.fromJson(line, User.class))
					.filter(userFromList -> !userFromList.getId().equals(id)).collect(Collectors.toList());
			String lines = userList.stream().map(userFromList -> gson.toJson(userFromList))
					.collect(Collectors.joining("\n"));
			deleteAllFrom(fileName);
			Files.write(Paths.get(USERS_DIRECTORY + fileName), lines.getBytes(StandardCharsets.UTF_8),
					getFileOption(fileName));
			logger.info("User with id {} was deleted from file: '{}'", id, fileName);
		} catch (IOException e) {
			logger.error(e.toString(), e);
			throw new RuntimeException();
		}

	}

	@Override
	public void deleteAllFrom(String fileName) {

		try (FileWriter fileWriter = new FileWriter(USERS_DIRECTORY + fileName, false);
				PrintWriter printWriter = new PrintWriter(fileWriter, false);) {
			printWriter.flush();
			logger.info("Users deleted from file '{}'", fileName);
		} catch (IOException e) {
			logger.error(e.toString(), e);
			e.printStackTrace();
		}

	}

	@Override
	public List<User> getUsers(String offset, String pageSize, String fileName) {
		try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(USERS_DIRECTORY + fileName));) {

			Integer minPosition = Integer.valueOf(offset) * Integer.valueOf(pageSize);
			Integer maxPosition = Integer.valueOf(offset) * Integer.valueOf(pageSize) + Integer.valueOf(pageSize);
			String line;
			List<User> usersList = new ArrayList<User>();

			logger.info("Users get from file {}", fileName);
			logger.info("offset {}, pageSize {}, min {}, max {}", Integer.valueOf(offset), Integer.valueOf(pageSize),
					minPosition, maxPosition);

			while ((line = lineNumberReader.readLine()) != null) {
				logger.info("Line number {}", lineNumberReader.getLineNumber());

				if ((lineNumberReader.getLineNumber() > minPosition)
						&& (lineNumberReader.getLineNumber() <= maxPosition)) {
					usersList.add(gson.fromJson(line, User.class));
					logger.info("User is added to list");
				}

				if (lineNumberReader.getLineNumber() > maxPosition) {
					break;
				}
			}
			return usersList;
		} catch (IOException e) {
			logger.error("Unable to get all users from file " + fileName + ": " + e.toString());
			throw new RuntimeException();
		}
	}
	// Make pagination 0,5 - 1,2,3,4,5
	// 1,5 - 6,7,8,9,10
	// 2,5 - 11,12,13,14,15
	// 1,3 - 4,5,6

	@Override
	public Optional<User> getById(String id, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			logger.info("Return info about user with id {} from '{}'", id, fileName);
			return stream.map(line -> gson.fromJson(line, User.class)).filter(u -> u.getId().equals(id)).findAny();
		} catch (IOException e) {
			logger.error("Unable to get user from file " + fileName + ": " + e.toString());
			throw new RuntimeException();
		}
	}

	@Override
	public void update(User user, String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(USERS_DIRECTORY + fileName))) {
			List<User> userList = new ArrayList<User>();
			stream.map(line -> gson.fromJson(line, User.class)).forEach(eachUser -> {
				if (!eachUser.getId().equals(user.getId())) {
					System.out.println(user.getId() + " " + eachUser.getId());
					userList.add(eachUser);
				} else {
					userList.add(user);
				}
			});
			deleteAllFrom(fileName);
			save(userList, fileName);
			logger.info("User {} with id {} updated in file '{}'", user.getName() + " " + user.getLastName(),
					user.getId(), fileName);
		} catch (IOException e) {
			logger.error(e.toString(), e);
			throw new RuntimeException();
		}
	}

}
