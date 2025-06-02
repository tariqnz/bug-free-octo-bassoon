document.addEventListener('DOMContentLoaded', () => {
    const todoInput = document.getElementById('todo-input');
    const addTaskBtn = document.getElementById('add-task-btn');
    const todoList = document.getElementById('todo-list');

    // Load todos from local storage
    loadTodos();

    addTaskBtn.addEventListener('click', addTodo);
    todoInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            addTodo();
        }
    });

    function addTodo() {
        const taskText = todoInput.value.trim();
        if (taskText === '') {
            alert('Please enter a task!');
            return;
        }
        createTodoItem(taskText);
        todoInput.value = '';
        saveTodos();
    }

    function createTodoItem(taskText, isCompleted = false) {
        const li = document.createElement('li');
        if (isCompleted) {
            li.classList.add('completed');
        }

        const taskSpan = document.createElement('span');
        taskSpan.classList.add('task-text');
        taskSpan.textContent = taskText;

        const actionsDiv = document.createElement('div');
        actionsDiv.classList.add('actions');

        const doneBtn = document.createElement('button');
        doneBtn.classList.add('done-btn');
        doneBtn.innerHTML = '&#x2714;'; // Checkmark
        doneBtn.addEventListener('click', toggleDone);

        const editBtn = document.createElement('button');
        editBtn.classList.add('edit-btn');
        editBtn.innerHTML = '&#x270E;'; // Pencil
        editBtn.addEventListener('click', editTask);

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('delete-btn');
        deleteBtn.innerHTML = '&#x1F5D1;'; // Trash can
        deleteBtn.addEventListener('click', deleteTask);

        actionsDiv.appendChild(doneBtn);
        actionsDiv.appendChild(editBtn);
        actionsDiv.appendChild(deleteBtn);

        li.appendChild(taskSpan);
        li.appendChild(actionsDiv);
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
        const taskSpan = taskItem.querySelector('.task-text');
        const currentText = taskSpan.textContent;

        const inputField = document.createElement('input');
        inputField.type = 'text';
        inputField.value = currentText;
        inputField.classList.add('edit-input'); // For potential specific styling

        // Replace span with input field
        taskSpan.replaceWith(inputField);
        inputField.focus();

        // Change edit button to save button
        const editButton = event.target;
        editButton.innerHTML = '&#x1F4BE;'; // Floppy disk for Save
        editButton.classList.remove('edit-btn');
        editButton.classList.add('save-btn'); // For potential different styling/logic

        // Temporarily remove old event listener and add new one for saving
        const newEditButton = editButton.cloneNode(true);
        editButton.parentNode.replaceChild(newEditButton, editButton);
        newEditButton.addEventListener('click', saveEditedTask);


        inputField.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                saveEditedTask(e, newEditButton, inputField, taskItem);
            }
        });
         inputField.addEventListener('blur', function(e) {
            // If input loses focus and it's not due to clicking the save button itself
            if (!taskItem.querySelector('.save-btn:hover')) {
                 saveEditedTask(e, newEditButton, inputField, taskItem, true); // True to revert if empty
            }
        });
    }

    function saveEditedTask(event, button, inputField, taskItem, revertIfEmpty = false) {
        const newText = inputField.value.trim();
        const originalText = inputField.defaultValue; // Store original for revert

        if (newText === '' && revertIfEmpty) {
             const taskSpan = document.createElement('span');
             taskSpan.classList.add('task-text');
             taskSpan.textContent = originalText; // Revert to original
             inputField.replaceWith(taskSpan);
        } else if (newText === '') {
            alert("Task cannot be empty!");
            inputField.focus(); // Keep focus if trying to save empty
            return;
        } else {
            const taskSpan = document.createElement('span');
            taskSpan.classList.add('task-text');
            taskSpan.textContent = newText;
            inputField.replaceWith(taskSpan);
        }

        // Change save button back to edit button
        button.innerHTML = '&#x270E;'; // Pencil
        button.classList.remove('save-btn');
        button.classList.add('edit-btn');

        // Re-attach the original editTask listener
        const newEditButton = button.cloneNode(true);
        button.parentNode.replaceChild(newEditButton, button);
        newEditButton.addEventListener('click', editTask);

        saveTodos();
    }

    // --- Local Storage Functions ---
    function saveTodos() {
        const todos = [];
        todoList.querySelectorAll('li').forEach(taskItem => {
            todos.push({
                text: taskItem.querySelector('.task-text').textContent,
                completed: taskItem.classList.contains('completed')
            });
        });
        localStorage.setItem('todos', JSON.stringify(todos));
    }

    function loadTodos() {
        const storedTodos = localStorage.getItem('todos');
        if (storedTodos) {
            const todos = JSON.parse(storedTodos);
            todos.forEach(todo => {
                createTodoItem(todo.text, todo.completed);
            });
        }
    }
});
