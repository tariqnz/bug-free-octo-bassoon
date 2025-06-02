document.addEventListener('DOMContentLoaded', () => {
    // Updated and new input field references
    const todoTitleInput = document.getElementById('todo-title-input');
    const todoTextInput = document.getElementById('todo-text-input'); // Was todoInput
    const todoDateInput = document.getElementById('todo-date-input');
    const todoTimeInput = document.getElementById('todo-time-input');
    const addTaskBtn = document.getElementById('add-task-btn');
    const todoList = document.getElementById('todo-list');

    loadTodos();

    addTaskBtn.addEventListener('click', addTodo);
    // Optional: Add Enter key listener for title or text input if desired
    todoTextInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) { // Allow Shift+Enter for new lines in textarea
            event.preventDefault(); // Prevent default Enter behavior (new line)
            addTodo();
        }
    });

    function addTodo() {
        const title = todoTitleInput.value.trim();
        const taskText = todoTextInput.value.trim();
        const date = todoDateInput.value;
        const time = todoTimeInput.value;

        if (title === '' || taskText === '') {
            alert('Please enter both a title and task details!');
            return;
        }
        // For now, createTodoItem will just log until its HTML part is built
        createTodoItem(title, taskText, date, time, false);

        todoTitleInput.value = '';
        todoTextInput.value = '';
        todoDateInput.value = '';
        todoTimeInput.value = '';
        saveTodos();
    }

    // createTodoItem will be fully fleshed out in the next step.
    // For now, let's make sure it accepts the parameters.
    // The actual DOM creation for the box will be in the next step.
    function createTodoItem(title, taskText, date, time, isCompleted = false) {
        const li = document.createElement('li');
        if (isCompleted) {
            li.classList.add('completed');
        }

        const itemBox = document.createElement('div');
        itemBox.classList.add('todo-item-box');

        const contentDiv = document.createElement('div');
        contentDiv.classList.add('task-content');

        const taskTitleElement = document.createElement('h3');
        taskTitleElement.classList.add('task-title');
        taskTitleElement.textContent = title;

        const taskDescriptionElement = document.createElement('p');
        taskDescriptionElement.classList.add('task-description');
        taskDescriptionElement.textContent = taskText;

        contentDiv.appendChild(taskTitleElement);
        contentDiv.appendChild(taskDescriptionElement);

        if (date || time) {
            const dateTimeDiv = document.createElement('div');
            dateTimeDiv.classList.add('task-datetime-details');

            if (date) {
                const taskDateElement = document.createElement('p');
                taskDateElement.classList.add('task-date');
                // Format date for better display if needed, e.g. using toLocaleDateString()
                // For now, direct value:
                taskDateElement.innerHTML = `<span class="label">Date:</span> ${date}`;
                dateTimeDiv.appendChild(taskDateElement);
            }

            if (time) {
                const taskTimeElement = document.createElement('p');
                taskTimeElement.classList.add('task-time');
                // Format time for better display if needed
                // For now, direct value:
                taskTimeElement.innerHTML = `<span class="label">Time:</span> ${time}`;
                dateTimeDiv.appendChild(taskTimeElement);
            }
            contentDiv.appendChild(dateTimeDiv);
        }

        itemBox.appendChild(contentDiv);

        const actionsDiv = document.createElement('div');
        actionsDiv.classList.add('actions');

        const doneBtn = document.createElement('button');
        doneBtn.classList.add('done-btn');
        doneBtn.innerHTML = '&#x2714; <span class="btn-text">Done</span>';
        doneBtn.addEventListener('click', toggleDone);

        const editBtn = document.createElement('button');
        editBtn.classList.add('edit-btn');
        editBtn.innerHTML = '&#x270E; <span class="btn-text">Edit</span>';
        editBtn.addEventListener('click', editTask);

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('delete-btn');
        deleteBtn.innerHTML = '&#x1F5D1; <span class="btn-text">Delete</span>';
        deleteBtn.addEventListener('click', deleteTask);

        actionsDiv.appendChild(doneBtn);
        actionsDiv.appendChild(editBtn);
        actionsDiv.appendChild(deleteBtn);

        itemBox.appendChild(actionsDiv);
        li.appendChild(itemBox);
        todoList.appendChild(li);
    }

    function toggleDone(event) {
        const taskItem = event.target.closest('li');
        taskItem.classList.toggle('completed');
        saveTodos();
    }

    function deleteTask(event) {
        const taskItem = event.target.closest('li');
        if (confirm('Are you sure you want to delete this task?')) {
            taskItem.remove();
            saveTodos();
        }
    }

    function editTask(event) {
        const taskItem = event.target.closest('li');
        const actionsDiv = taskItem.querySelector('.actions');
        const editButton = taskItem.querySelector('.edit-btn');

        // Assuming task-title and task-description elements exist from createTodoItem (next step)
        const titleElement = taskItem.querySelector('.task-title');
        const textElement = taskItem.querySelector('.task-description');

        if (!titleElement || !textElement) {
            console.error("Title or text element not found for editing.");
            return;
        }

        const currentTitle = titleElement.textContent;
        const currentText = textElement.textContent;

        const titleInput = document.createElement('input');
        titleInput.type = 'text';
        titleInput.value = currentTitle;
        titleInput.classList.add('edit-title-input'); // For specific styling

        const textInput = document.createElement('textarea');
        textInput.value = currentText;
        textInput.classList.add('edit-text-input'); // For specific styling
        textInput.rows = 3;

        titleElement.replaceWith(titleInput);
        textElement.replaceWith(textInput);
        titleInput.focus();

        editButton.innerHTML = '&#x1F4BE;'; // Save icon
        editButton.removeEventListener('click', editTask); // Remove old listener
        editButton.addEventListener('click', () => saveEditedTask(taskItem, titleInput, textInput, editButton));
    }

    function saveEditedTask(taskItem, titleInput, textInput, saveButton) {
        const newTitle = titleInput.value.trim();
        const newText = textInput.value.trim();

        if (newTitle === '' || newText === '') {
            alert('Title and task details cannot be empty!');
            return;
        }

        const newTitleElement = document.createElement('h3'); // Will be styled as .task-title
        newTitleElement.classList.add('task-title');
        newTitleElement.textContent = newTitle;

        const newTextElement = document.createElement('p'); // Will be styled as .task-description
        newTextElement.classList.add('task-description');
        newTextElement.textContent = newText;

        titleInput.replaceWith(newTitleElement);
        textInput.replaceWith(newTextElement);

        saveButton.innerHTML = '&#x270E;'; // Edit icon
        saveButton.removeEventListener('click', () => saveEditedTask(taskItem, titleInput, textInput, saveButton)); // Not ideal, better to replace node or manage listeners more robustly if this were more complex
        saveButton.addEventListener('click', editTask);

        saveTodos();
    }

    function saveTodos() {
        const todos = [];
        todoList.querySelectorAll('li').forEach(taskItem => {
            // These selectors will need to be robust based on the final structure in createTodoItem
            const titleEl = taskItem.querySelector('.task-title');
            const textEl = taskItem.querySelector('.task-description');
            const dateEl = taskItem.querySelector('.task-date'); // Assuming these classes exist
            const timeEl = taskItem.querySelector('.task-time');

            // Fallback if elements are inputs during edit (though ideally save before this runs)
            const title = titleEl ? titleEl.textContent : taskItem.querySelector('.edit-title-input')?.value;
            const text = textEl ? textEl.textContent : taskItem.querySelector('.edit-text-input')?.value;

            // Extract date/time, removing prefixes if they exist from the temporary display
            const dateText = dateEl ? dateEl.textContent.replace('Date: ', '') : '';
            const timeText = timeEl ? timeEl.textContent.replace('Time: ', '') : '';

            if (title && text) { // Only save if title and text are valid
                 todos.push({
                    title: title,
                    text: text,
                    date: dateText,
                    time: timeText,
                    completed: taskItem.classList.contains('completed')
                });
            }
        });
        localStorage.setItem('todos', JSON.stringify(todos));
    }

    function loadTodos() {
        const storedTodos = localStorage.getItem('todos');
        if (storedTodos) {
            const todos = JSON.parse(storedTodos);
            todos.forEach(todo => {
                // Ensure all data fields are passed
                createTodoItem(todo.title, todo.text, todo.date, todo.time, todo.completed);
            });
        }
    }
});
