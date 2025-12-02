document.addEventListener('DOMContentLoaded', () => {
    // --- Global State ---
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    let currentWordId = null;
    let currentUserVocabId = null;

    // --- Elements ---
    const searchBtn = document.getElementById('searchBtn');
    const wordInput = document.getElementById('wordInput');
    const loadingDiv = document.getElementById('loading');
    const resultContainer = document.getElementById('resultContainer');
    const errorDiv = document.getElementById('error');
    const errorMessage = document.getElementById('errorMessage');

    // Auth Elements
    const userSection = document.getElementById('userSection');
    const guestSection = document.getElementById('guestSection');
    const welcomeMsg = document.getElementById('welcomeMsg');
    const logoutBtn = document.getElementById('logoutBtn');
    const historySidebar = document.getElementById('historySidebar');
    const historyList = document.getElementById('historyList');
    const notebookList = document.getElementById('notebookList');

    // Notebook Elements
    const notebookSection = document.getElementById('notebookSection');
    const userNoteInput = document.getElementById('userNote');
    const userExampleInput = document.getElementById('userExample');
    const saveNoteBtn = document.getElementById('saveNoteBtn');

    // --- Initialization ---
    initAuth();

    // --- Event Listeners ---
    if (searchBtn) {
        searchBtn.addEventListener('click', handleSearch);
        wordInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') handleSearch();
        });
    }

    if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
    }

    if (saveNoteBtn) {
        saveNoteBtn.addEventListener('click', handleSaveNote);
    }

    // Login Page Logic
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Register Page Logic
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }

    // --- Auth Functions ---
    function initAuth() {
        if (currentUser) {
            if (userSection) userSection.classList.remove('hidden');
            if (guestSection) guestSection.classList.add('hidden');
            if (welcomeMsg) welcomeMsg.textContent = `Hello, ${currentUser.name || currentUser.username}`;
            if (historySidebar) {
                historySidebar.classList.remove('hidden');
                loadHistory();
            }
        } else {
            if (userSection) userSection.classList.add('hidden');
            if (guestSection) guestSection.classList.remove('hidden');
            if (historySidebar) historySidebar.classList.add('hidden');
        }
    }

    async function handleLogin(e) {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const authError = document.getElementById('authError');

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, passwordHash: password }) // Note: using passwordHash field for plain password as per backend
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('currentUser', JSON.stringify(data.user));
                localStorage.setItem('authToken', data.token);
                window.location.href = 'index.html';
            } else {
                authError.classList.remove('hidden');
            }
        } catch (error) {
            console.error('Login error:', error);
            authError.classList.remove('hidden');
        }
    }

    async function handleRegister(e) {
        e.preventDefault();
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const authError = document.getElementById('authError');

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, passwordHash: password })
            });

            if (response.ok) {
                alert('Registration successful! Please login.');
                window.location.href = 'login.html';
            } else {
                authError.textContent = 'Registration failed. Email might be taken.';
                authError.classList.remove('hidden');
            }
        } catch (error) {
            console.error('Register error:', error);
            authError.classList.remove('hidden');
        }
    }

    function handleLogout() {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('authToken');
        window.location.reload();
    }

    // --- Search & History Functions ---
    async function handleSearch() {
        const word = wordInput.value.trim();
        if (!word) return;

        // Reset UI
        hide(resultContainer);
        hide(errorDiv);
        show(loadingDiv);
        if (notebookSection) hide(notebookSection);

        try {
            const token = localStorage.getItem('authToken');
            const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

            const response = await fetch(`/api/words/lookup?word=${encodeURIComponent(word)}`, {
                headers: headers
            });

            if (!response.ok) {
                throw new Error(`Server returned status: ${response.status}`);
            }

            const data = await response.json();
            currentWordId = data.id;
            displayResult(data);

            // Save to history if logged in
            if (currentUser && data.id) {
                await addToHistory(data.id);
                loadUserNote(data.id);
            }

        } catch (error) {
            showError(error.message);
        } finally {
            hide(loadingDiv);
        }
    }

    async function addToHistory(wordId) {
        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`/api/user-vocab/user/${currentUser.id}/word/${wordId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({}) // Empty body for now
            });

            if (response.ok) {
                const entry = await response.json();
                currentUserVocabId = entry.id;
                loadHistory(); // Refresh list
            }
        } catch (e) {
            console.error('Failed to save history', e);
        }
    }

    async function loadUserNote(wordId) {
        if (!currentUser) return;
        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`/api/user-vocab/user/${currentUser.id}/word/${wordId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const entry = await response.json();
                currentUserVocabId = entry.id;

                // Populate Notebook
                if (userNoteInput) userNoteInput.value = entry.userNote || '';
                if (userExampleInput) userExampleInput.value = entry.userExample || '';
                if (notebookSection) show(notebookSection);
            }
        } catch (e) {
            console.error('Failed to load user note', e);
        }
    }

    async function handleSaveNote() {
        if (!currentUserVocabId) return;

        const note = userNoteInput.value;
        const example = userExampleInput.value;

        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`/api/user-vocab/${currentUserVocabId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    userNote: note,
                    userExample: example,
                    favorite: false // Keep favorite as is (TODO: add favorite toggle)
                })
            });

            if (response.ok) {
                alert('Note saved successfully!');
            } else {
                alert('Failed to save note.');
            }
        } catch (e) {
            console.error('Error saving note', e);
            alert('Error saving note.');
        }
    }

    async function loadHistory() {
        if (!historyList || !notebookList) return;
        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`/api/user-vocab/user/${currentUser.id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const entries = await response.json();
                renderNotebookList(entries);
                renderHistoryList(entries);
            }
        } catch (e) {
            console.error('Failed to load history', e);
        }
    }

    function renderNotebookList(entries) {
        notebookList.innerHTML = '';
        // Filter entries with notes or examples
        const savedEntries = entries.filter(e => e.userNote || e.userExample);
        savedEntries.sort((a, b) => new Date(b.updatedAt || b.createdAt) - new Date(a.updatedAt || a.createdAt));

        if (savedEntries.length === 0) {
            notebookList.innerHTML = '<li class="text-muted" style="padding:0.5rem; cursor:default;">No saved notes yet.</li>';
            return;
        }

        savedEntries.forEach(entry => {
            const li = document.createElement('li');
            li.innerHTML = `<i class="fa-solid fa-book-bookmark"></i> ${entry.word}`;
            li.onclick = () => {
                wordInput.value = entry.word;
                handleSearch();
            };
            notebookList.appendChild(li);
        });
    }

    function renderHistoryList(entries) {
        historyList.innerHTML = '';
        // Sort by newest first (using updatedAt if available for duplicates)
        entries.sort((a, b) => new Date(b.updatedAt || b.createdAt) - new Date(a.updatedAt || a.createdAt));

        entries.forEach(entry => {
            const li = document.createElement('li');
            li.className = 'history-item';

            const wordSpan = document.createElement('span');
            wordSpan.innerHTML = `<i class="fa-solid fa-clock-rotate-left"></i> ${entry.word}`;
            wordSpan.onclick = () => {
                wordInput.value = entry.word;
                handleSearch();
            };

            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'delete-btn';
            deleteBtn.innerHTML = '<i class="fa-solid fa-times"></i>';
            deleteBtn.onclick = (e) => {
                e.stopPropagation();
                deleteHistoryItem(entry.id);
            };

            li.appendChild(wordSpan);
            li.appendChild(deleteBtn);
            historyList.appendChild(li);
        });
    }

    async function deleteHistoryItem(id) {
        if (!confirm('Are you sure you want to delete this item?')) return;

        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`/api/user-vocab/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                loadHistory(); // Refresh lists
            } else {
                alert('Failed to delete item.');
            }
        } catch (e) {
            console.error('Error deleting item', e);
        }
    }

    // --- Helper Functions ---
    function displayResult(data) {
        // Update DOM elements
        document.getElementById('resultWord').textContent = data.word;
        document.getElementById('resultDefinition').textContent = data.definition;
        document.getElementById('resultUsage').textContent = data.ieltsUsage || 'Band 6.0';
        document.getElementById('resultBand7').textContent = data.band7SampleSentence;

        // Parse and display lists
        renderList('resultExamples', data.exampleSentencesJson);
        renderTags('resultSynonyms', data.synonymsJson);
        renderTags('resultAntonyms', data.antonymsJson);

        show(resultContainer);
    }

    function renderList(elementId, jsonString) {
        const list = document.getElementById(elementId);
        list.innerHTML = '';
        try {
            const items = JSON.parse(jsonString);
            if (Array.isArray(items)) {
                items.forEach(item => {
                    const li = document.createElement('li');
                    li.textContent = item;
                    list.appendChild(li);
                });
            }
        } catch (e) {
            console.error('Error parsing list JSON', e);
        }
    }

    function renderTags(elementId, jsonString) {
        const container = document.getElementById(elementId);
        container.innerHTML = '';
        try {
            const items = JSON.parse(jsonString);
            if (Array.isArray(items) && items.length > 0) {
                items.forEach(item => {
                    const span = document.createElement('span');
                    span.className = 'tag';
                    span.textContent = item;
                    container.appendChild(span);
                });
            } else {
                container.innerHTML = '<span class="text-muted">None</span>';
            }
        } catch (e) {
            console.error('Error parsing tags JSON', e);
        }
    }

    function showError(message) {
        errorMessage.textContent = message;
        show(errorDiv);
    }

    function show(element) {
        if (element) element.classList.remove('hidden');
    }

    function hide(element) {
        if (element) element.classList.add('hidden');
    }
});
