document.addEventListener("DOMContentLoaded", function () {
  loadTasks();
});

function loadTasks() {
  fetch("http://localhost:8080/TasksMaster/tasks")
    .then((response) => response.json())
    .then((tasks) => {
      tasks.sort((a, b) => compareCompletedAndDueDates(a, b));
      displayTasks(tasks);
    });
}

function compareCompletedAndDueDates(taskA, taskB) {
  // Sort by completed status (incomplete tasks first) and then by due date
  if (taskA.status === taskB.status) {
    return compareDueDates(taskA.dueDate, taskB.dueDate);
  }

  // Completed tasks go to the end
  return taskA.status ? 1 : -1;
}

function compareDueDates(dateA, dateB) {
  // Compare two dates for sorting (null dates are considered greater)
  if (!dateA && !dateB) return 0;
  if (!dateA) return 1;
  if (!dateB) return -1;

  return new Date(dateA) - new Date(dateB);
}

function displayTasks(tasks) {
  const taskList = document.getElementById("taskList");
  taskList.innerHTML = "";

  tasks.forEach((task) => {
    const listItem = document.createElement("li");
    const wrapper = document.createElement("div");
    const box = document.createElement("div");
    const taskDiv = document.createElement("div");
    taskDiv.classList.add("task-item");
    wrapper.classList.add("wrapper");

    const checkbox = document.createElement("input");
    checkbox.classList.add("completion-tick");
    checkbox.classList.add("form-check-input");

    checkbox.type = "checkbox";
    checkbox.checked = task.status;

    checkbox.addEventListener("change", function () {
      updateTaskStatus(task.id, checkbox.checked);
    });

    const dueDateText = document.createElement("span");
    dueDateText.classList.add("due-date"); // Apply CSS class
    dueDateText.textContent = task.dueDate ? formatDate(task.dueDate) : "";

    box.appendChild(checkbox);
    box.appendChild(document.createTextNode(task.description));
    wrapper.appendChild(box);

    if (task.status) {
      wrapper.classList.add("completed-task");
    } else {
      wrapper.appendChild(dueDateText);
    }

    taskDiv.appendChild(wrapper);

    taskDiv.addEventListener("click", function () {
      if (!task.status) {
        updateTaskStatus(task.id, true);
      }
    });

    listItem.appendChild(taskDiv);
    taskList.appendChild(listItem);
  });
}

function addTask() {
  const taskInput = document.getElementById("taskInput");
  const dueDateInput = document.getElementById("dueDateInput");

  const description = taskInput.value.trim();
  const dueDate = dueDateInput.value;

  if (description !== "") {
    const newTask = {
      description: description,
      status: false, // CREATING A DEFAULT FALSE
      dueDate: dueDate || null, // CREATING  A DEFAULT NULL
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

function formatDate(dateString) {
  const options = { year: "numeric", month: "2-digit", day: "2-digit" };
  return new Date(dateString).toLocaleDateString(undefined, options);
}
