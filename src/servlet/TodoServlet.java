package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Task;
import utils.DBUtility;

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

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Delete completed tasks from the database
		deleteCompletedTasks();

		// Return the updated task list as JSON
		List<Task> tasks = getTasksFromDatabase();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonTasks = objectMapper.writeValueAsString(tasks);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonTasks);
	}

	private List<Task> getTasksFromDatabase() {
		List<Task> tasks = new ArrayList<>();
		Connection connection = null;
		try {
			connection = DBUtility.getConnection();
			String query = "SELECT * FROM tasks";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Task task = new Task();
				task.setId(resultSet.getInt("id"));
				task.setDescription(resultSet.getString("description"));
				task.setStatus(resultSet.getBoolean("status"));
				task.setDueDate(resultSet.getDate("due_date"));
				tasks.add(task);
			}

			resultSet.close();
			preparedStatement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(connection);

		}
		return tasks;

	}

	private void updateTaskInDatabase(Task task) {
		Connection connection = null;
		try {
			connection = DBUtility.getConnection();

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
		} finally {
			DBUtility.closeConnection(connection);
		}
	}

	private void addTaskToDatabase(Task task) {
		Connection connection = null;
		try {
			connection = DBUtility.getConnection();
			String insertQuery = "INSERT INTO tasks (description, status, due_date) VALUES (?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
				preparedStatement.setString(1, task.getDescription());
				preparedStatement.setBoolean(2, task.isStatus());
//				preparedStatement.setTimestamp(3, new Timestamp(task.getCreationDate().getTime()));

				// Set due date to null if not provided
				if (task.getDueDate() != null) {
					preparedStatement.setTimestamp(3, new Timestamp(task.getDueDate().getTime()));
				} else {
					preparedStatement.setNull(3, Types.TIMESTAMP);
				}

				preparedStatement.executeUpdate();
			}

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(connection);
		}
	}

	private void deleteCompletedTasks() {
		Connection connection = null;
		try {
			connection = DBUtility.getConnection();

			String deleteQuery = "DELETE FROM tasks WHERE status = true";
			try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
				preparedStatement.executeUpdate();
			}

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.closeConnection(connection);
		}
	}
}
