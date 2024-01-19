document.addEventListener("DOMContentLoaded", function () {
  loadTasks();
});

function loadTasks() {
  fetch("http://localhost:8080/TasksMaster/tasks")
    .then((response) => response.json())
    .then((tasks) => displayTasks(tasks));
}

function displayTasks(tasks) {
  const taskList = document.getElementById("taskList");
  taskList.innerHTML = "";

  tasks.forEach((task) => {
    const listItem = document.createElement("li");
    const taskDiv = document.createElement("div");
    taskDiv.classList.add("task-item"); // Add a CSS class for styling

    const checkbox = document.createElement("input");
    checkbox.classList.add("completion-tick");

    checkbox.type = "checkbox";
    checkbox.checked = task.status;

    // Add a "change" event listener to handle checkbox clicks
    checkbox.addEventListener("change", function () {
      updateTaskStatus(task.id, checkbox.checked);
    });

    taskDiv.appendChild(checkbox);
    taskDiv.appendChild(document.createTextNode(task.description));

    // Add a "click" event listener to mark the task as completed
    taskDiv.addEventListener("click", function () {
      if (!task.status) {
        // Only mark as completed if not already completed
        updateTaskStatus(task.id, true);
      }
    });

    listItem.appendChild(taskDiv);
    taskList.appendChild(listItem);
  });
}

function addTask() {
  const taskInput = document.getElementById("taskInput");
  const description = taskInput.value.trim();

  if (description !== "") {
    const newTask = {
      description: description,
      status: false, // CREATING A DEFAULT FALSE
    };

    fetch("http://localhost:8080/TasksMaster/tasks", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newTask),
    })
      .then((response) => response.json())
      .then((tasks) => displayTasks(tasks))
      .catch((error) => console.error("Error:", error));

    taskInput.value = "";
  }
}

function updateTaskStatus(taskId, status) {
  const updateData = {
    id: taskId,
    status: status,
  };

  fetch("http://localhost:8080/TasksMaster/tasks", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updateData),
  })
    .then((response) => response.json())
    .then((tasks) => displayTasks(tasks))
    .catch((error) => console.error("Error:", error));
}

function deleteCompletedTasks() {
  // Send a request to the server to delete completed tasks
  fetch("http://localhost:8080/TasksMaster/tasks", {
    method: "DELETE",
  })
    .then((response) => response.json())
    .then((tasks) => displayTasks(tasks))
    .catch((error) => console.error("Error:", error));
}
