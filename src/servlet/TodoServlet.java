package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tasks")
public class TodoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Task> tasks = getTasksFromDatabase();

		// Convert the List<Task> to a JSON array
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonTasks = objectMapper.writeValueAsString(tasks);

		// Set the content type and write the JSON response
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonTasks);

	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		// Convert the JSON request body to a Task object
		ObjectMapper objectMapper = new ObjectMapper();
		Task updateTask = objectMapper.readValue(requestBody, Task.class);

		// Update the task status in the database
		updateTaskInDatabase(updateTask);

		// Return the updated task list as JSON
		List<Task> tasks = getTasksFromDatabase();
		String jsonTasks = objectMapper.writeValueAsString(tasks);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonTasks);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		// Convert the JSON request body to a Task object
		ObjectMapper objectMapper = new ObjectMapper();
		Task newTask = objectMapper.readValue(requestBody, Task.class);

		// Add the new task to the database
		addTaskToDatabase(newTask);

		// Return the updated task list as JSON
		List<Task> tasks = getTasksFromDatabase();
		String jsonTasks = objectMapper.writeValueAsString(tasks);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonTasks);
	}

	private List<Task> getTasksFromDatabase() {
		List<Task> tasks = new ArrayList<>();

		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tasklist", "postgres",
					"shourya1311");
			String query = "SELECT * FROM tasks";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String description = resultSet.getString("description");
				boolean status = resultSet.getBoolean("status");

				Task task = new Task(id, description, status);
				tasks.add(task);
			}

			resultSet.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tasks;
	}

	private void updateTaskInDatabase(Task task) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tasklist", "postgres",
					"shourya1311");
			String updateQuery = "UPDATE tasks SET status= ? WHERE id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
				preparedStatement.setBoolean(1, task.isStatus());
				preparedStatement.setInt(2, task.getId());

				preparedStatement.executeUpdate();
				preparedStatement.close();
			}

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addTaskToDatabase(Task task) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tasklist", "postgres",
					"shourya1311");

			String insertQuery = "INSERT INTO tasks (description, status) VALUES (?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
				preparedStatement.setString(1, task.getDescription());
				preparedStatement.setBoolean(2, task.isStatus());

				preparedStatement.executeUpdate();
			}

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
