document.addEventListener('DOMContentLoaded', () => {
    // DOM References
    const addTaskForm = document.getElementById('add-task-form');
    const taskTitleInput = document.getElementById('task-title');
    const taskDescriptionInput = document.getElementById('task-description');
    const taskDueDateInput = document.getElementById('task-due-date');
    const taskDueTimeInput = document.getElementById('task-due-time');
    const taskListUL = document.getElementById('task-list');

    // In-memory store for tasks
    let tasks = [];

    // --- Task Management Functions ---

    function addTask(event) {
        event.preventDefault(); // Prevent form submission from reloading page

        const title = taskTitleInput.value.trim();
        const description = taskDescriptionInput.value.trim();
        const dueDate = taskDueDateInput.value;
        const dueTime = taskDueTimeInput.value;

        if (!title) {
            alert('Task title is required!');
            return;
        }

        const newTask = {
            id: Date.now().toString(), // Simple unique ID
            title: title,
            description: description,
            dueDate: dueDate,
            dueTime: dueTime,
            isCompleted: false,
            createdAt: new Date().toISOString() // Optional: for sorting later
        };

        tasks.push(newTask);
        renderTasks();
        saveTasksToLocalStorage();
        addTaskForm.reset(); // Clear form fields
        taskTitleInput.focus();
    }

    function renderTasks() {
        taskListUL.innerHTML = ''; // Clear existing tasks

        if (tasks.length === 0) {
            taskListUL.innerHTML = '<li class="no-tasks-message">No tasks yet. Add one above!</li>';
            return;
        }

        tasks.forEach(task => {
            const li = document.createElement('li');
            li.className = `task-item ${task.isCompleted ? 'completed' : ''}`;
            li.setAttribute('data-task-id', task.id);

            // Basic structure for Phase 1 - styling comes later
            li.innerHTML = `
                <div class="task-details">
                    <h3>${task.title}</h3>
                    <p class="desc">${task.description || 'No description'}</p>
                    ${task.dueDate ? `<p class="date-time">Due: ${formatDisplayDate(task.dueDate)} ${task.dueTime || ''}</p>` : ''}
                </div>
                <div class="task-actions">
                    <button class="complete-btn" title="${task.isCompleted ? 'Mark as Incomplete' : 'Mark as Complete'}">
                        ${task.isCompleted
                            ? '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>' // Undo (X icon)
                            : '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>' // Complete (Checkmark icon)
                        }
                    </button>
                    <button class="edit-btn" title="Edit Task">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>
                    </button>
                    <button class="delete-btn" title="Delete Task">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                    </button>
                </div>
            `;

            // Add event listeners to buttons
            li.querySelector('.complete-btn').addEventListener('click', () => toggleComplete(task.id));
            li.querySelector('.edit-btn').addEventListener('click', () => handleEdit(task.id));
            li.querySelector('.delete-btn').addEventListener('click', () => deleteTask(task.id));

            taskListUL.appendChild(li);
        });
    }

    const inputArea = document.getElementById('input-area'); // Cache input area for global editing class

    function deleteTask(taskId) {
        const taskItem = taskListUL.querySelector(`[data-task-id="${taskId}"]`);
        if (taskItem && confirm('Are you sure you want to delete this task?')) {
            taskItem.classList.add('task-deleted'); // Add animation class

            taskItem.addEventListener('animationend', () => {
                tasks = tasks.filter(task => task.id !== taskId);
                renderTasks(); // Re-render after animation. Could also just taskItem.remove() if no other dependencies.
                saveTasksToLocalStorage();
            }, { once: true }); // Ensure listener is called only once
        }
    }

    function toggleComplete(taskId) {
        tasks = tasks.map(task => {
            if (task.id === taskId) {
                return { ...task, isCompleted: !task.isCompleted };
            }
            return task;
        });
        renderTasks();
        saveTasksToLocalStorage();
    }

    function handleEdit(taskId) {
        const taskItem = taskListUL.querySelector(`[data-task-id="${taskId}"]`);
        if (!taskItem) return;

        const taskToEdit = tasks.find(task => task.id === taskId);
        if (!taskToEdit) return;

        // Prevent multiple edits at once / check if already editing
        if (taskItem.classList.contains('editing')) return;
        taskItem.classList.add('editing');
        inputArea.classList.add('editing-global'); // Disable main form

        const taskDetailsDiv = taskItem.querySelector('.task-details');
        const taskActionsDiv = taskItem.querySelector('.task-actions');

        // Store original actions to restore later
        const originalActionsHTML = taskActionsDiv.innerHTML;

        // Create input fields
        taskDetailsDiv.innerHTML = `
            <input type="text" class="edit-task-title" value="${taskToEdit.title}" required>
            <textarea class="edit-task-description">${taskToEdit.description}</textarea>
            <div class="edit-task-datetime">
                <input type="date" class="edit-task-due-date" value="${taskToEdit.dueDate || ''}">
                <input type="time" class="edit-task-due-time" value="${taskToEdit.dueTime || ''}">
            </div>
        `;

        // Change action buttons
        taskActionsDiv.innerHTML = `
            <button class="save-edit-btn" title="Save Changes">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm2 16H5V5h11.17L19 7.83V19zm-7-7c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3zM6 6h9v4H6z"/></svg> <!-- Save Icon -->
            </button>
            <button class="cancel-edit-btn" title="Cancel Edit">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg> <!-- Cancel Icon (X) -->
            </button>
        `;

        taskActionsDiv.querySelector('.save-edit-btn').addEventListener('click', () => saveEditedTask(taskId, taskItem));
        taskActionsDiv.querySelector('.cancel-edit-btn').addEventListener('click', () => {
            taskItem.classList.remove('editing');
            inputArea.classList.remove('editing-global'); // Re-enable main form
            renderTasks(); // Just re-render to cancel, could optimize to restore original HTML
        });
    }

    function saveEditedTask(taskId, taskItemElement) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        if (taskIndex === -1) return;

        const titleInput = taskItemElement.querySelector('.edit-task-title');
        const descriptionInput = taskItemElement.querySelector('.edit-task-description');
        const dueDateInput = taskItemElement.querySelector('.edit-task-due-date');
        const dueTimeInput = taskItemElement.querySelector('.edit-task-due-time');

        const newTitle = titleInput.value.trim();
        if (!newTitle) {
            alert('Task title cannot be empty!');
            titleInput.focus();
            return;
        }

        tasks[taskIndex] = {
            ...tasks[taskIndex],
            title: newTitle,
            description: descriptionInput.value.trim(),
            dueDate: dueDateInput.value,
            dueTime: dueTimeInput.value
        };

        taskItemElement.classList.remove('editing'); // Remove editing class
        inputArea.classList.remove('editing-global'); // Re-enable main form
        renderTasks(); // Re-render the whole list to reflect changes
        saveTasksToLocalStorage();
    }

    // --- Utility Functions ---
    function formatDisplayDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        // Make sure to account for timezone offset to display the selected date correctly
        const userTimezoneOffset = date.getTimezoneOffset() * 60000;
        return new Date(date.getTime() + userTimezoneOffset).toLocaleDateString(undefined, {
            year: 'numeric', month: 'long', day: 'numeric'
        });
    }


    // --- Local Storage ---
    function saveTasksToLocalStorage() {
        localStorage.setItem('modernTasks', JSON.stringify(tasks));
    }

    function loadTasksFromLocalStorage() {
        const storedTasks = localStorage.getItem('modernTasks');
        if (storedTasks) {
            tasks = JSON.parse(storedTasks);
        }
    }

    // --- Initial Setup ---
    addTaskForm.addEventListener('submit', addTask);
    loadTasksFromLocalStorage();
    renderTasks(); // Initial render of tasks from local storage
});
