async function httpPost(url, body) {
    const res = await fetch(url, {
        method: "POST",
        body: body,
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        credentials: 'same-origin'
    })

    return res.status;
}

async function fetchAndDisplayUsers() {
	const usersSection = document.querySelector('.users-section');
	if (!usersSection) {
		console.error('Users section container not found');
		return;
	}

	// Show skeleton loading
	usersSection.innerHTML = `
		<div class="users-table-container">
			<table class="users-table">
				<thead>
					<tr>
						<th>Nome</th>
						<th>Email</th>
						<th>Matrícula</th>
						<th>Curso</th>
						<th>Tipo</th>
						<th>Ações</th>
					</tr>
				</thead>
				<tbody>
					<tr><td colspan="6"><div class="skeleton skeleton-text"></div></td></tr>
					<tr><td colspan="6"><div class="skeleton skeleton-text"></div></td></tr>
					<tr><td colspan="6"><div class="skeleton skeleton-text"></div></td></tr>
				</tbody>
			</table>
		</div>
	`;

	try {
		const response = await fetch('/api/admin/users', { 
			credentials: 'same-origin'
		});
		
		if (!response.ok) throw new Error('Network response was not ok');
		const users = await response.json();

		const currentUserResponse = await fetch('/api/me', {
			credentials: 'same-origin'
		});
		if (!currentUserResponse.ok) throw new Error('Network response was not ok');
		
		const currentUser = await currentUserResponse.json();
		console.log("Usuário atual:", currentUser.email);

		
		// Find the users-section container
		const usersSection = document.querySelector('.users-section');
		if (!usersSection) {
			console.error('Users section container not found');
			return;
		}

		// Create table container with project styling
		const tableContainer = document.createElement('div');
		tableContainer.className = 'users-table-container';

		const table = document.createElement('table');
		table.className = 'users-table';

		// Create table header
		const thead = document.createElement('thead');
		const headerRow = document.createElement('tr');
		const headers = ['Nome', 'Email', 'Matrícula', 'Curso', 'Tipo', 'Ações'];
		
		headers.forEach(headerText => {
			const th = document.createElement('th');
			th.textContent = headerText;
			headerRow.appendChild(th);
		});
		thead.appendChild(headerRow);
		table.appendChild(thead);

		// Create table body
		const tbody = document.createElement('tbody');
		
		if (users.length === 0) {
			const row = document.createElement('tr');
			const td = document.createElement('td');
			td.setAttribute('colspan', '6');
			td.className = 'no-users';
			td.textContent = 'Nenhum usuário cadastrado';
			row.appendChild(td);
			tbody.appendChild(row);
		} else {
			users.forEach(user => {
				const row = document.createElement('tr');
				row.id = user.email;
				
				// Nome
				const nameCell = document.createElement('td');
				nameCell.textContent = user.nome || 'N/A';
				row.appendChild(nameCell);
				
				// Email
				const emailCell = document.createElement('td');
				emailCell.textContent = user.email || 'N/A';
				row.appendChild(emailCell);
				
				// Matrícula
				const matriculaCell = document.createElement('td');
				matriculaCell.textContent = user.matricula || 'N/A';
				row.appendChild(matriculaCell);
				
				// Curso
				const cursoCell = document.createElement('td');
				cursoCell.textContent = user.curso || 'N/A';
				row.appendChild(cursoCell);
				
				// Tipo (Admin/User)
				const typeCell = document.createElement('td');
				const typeSpan = document.createElement('span');
				typeSpan.className = user.admin ? 'user-type admin' : 'user-type user';
				typeSpan.textContent = user.admin ? 'Admin' : 'Usuário';
				typeCell.appendChild(typeSpan);
				row.appendChild(typeCell);
				
				// Ações
				const actionsCell = document.createElement('td');
				actionsCell.className = 'actions';
				
				// Toggle Admin button
				const deleteButton = document.createElement('button');
				const toggleButton = document.createElement('button');
				console.log("Carregando usuário:", user.email);
				if (currentUser.email === user.email) {
					toggleButton.disabled = true;
					deleteButton.disabled = true;
					// toggleButton.style.display = 'none';
					// deleteButton.style.display = 'none';
				}
				toggleButton.type = 'button';
				toggleButton.onclick = async (event) => {
					event.preventDefault();
					
					// Add loading state
					toggleButton.disabled = true;
					const originalText = toggleButton.textContent;
					toggleButton.classList.add('btn-loading');
					toggleButton.textContent = 'Processando...';
					
					const code = await httpPost("/api/admin/toggle-admin", JSON.stringify({ email: user.email }));
					
					// Remove loading state
					toggleButton.disabled = false;
					toggleButton.classList.remove('btn-loading');
					
					if (code === 200) {
						// Refresh the user list to reflect changes
						typeSpan.className = !user.admin ? 'user-type admin' : 'user-type user';
						typeSpan.textContent = !user.admin ? 'Admin' : 'Usuário';
						toggleButton.textContent = !user.admin ? 'Remover Admin' : 'Tornar Admin';
						user.admin = !user.admin; // Update local state
						// fetchAndDisplayUsers();
					} else if (code === 400) {
						toggleButton.textContent = originalText;
						alert('Erro: Não é possível alterar o status de admin do próprio usuário.');
					} else if (code === 401) {
						alert('Sessão expirada. Por favor refaça login.');
						document.location.href = '/login?error=notAuthenticated';
					} else {
						toggleButton.textContent = originalText;
						alert('Erro ao alterar status de admin. Tente novamente.');
					}
				}
				toggleButton.className = 'btn btn-secondary';
				toggleButton.textContent = user.admin ? 'Remover Admin' : 'Tornar Admin';
				actionsCell.appendChild(toggleButton);
				
				// Delete button

				deleteButton.type = 'button';
				deleteButton.onclick = async (event) => {
					event.preventDefault();
					if (confirm(`Confirma a exclusão do usuário ${user.nome}? Esta ação não pode ser desfeita.`)) {
						// Add loading state
						deleteButton.disabled = true;
						deleteButton.classList.add('btn-loading');
						const originalText = deleteButton.textContent;
						deleteButton.textContent = 'Excluindo...';
						
						const code = await httpPost("/api/admin/delete-user", JSON.stringify({ email: user.email }));
						
						// Remove loading state
						deleteButton.disabled = false;
						deleteButton.classList.remove('btn-loading');
						deleteButton.textContent = originalText;
						
						if (code === 200) {
							// Refresh the user list to reflect changes
							row.remove();
							// fetchAndDisplayUsers();
						} else if (code === 400) {
							alert('Erro: Não é possível excluir o próprio usuário.');
						} else if (code === 401) {
							alert('Sessão expirada. Por favor refaça login.');
							document.location.href = '/login?error=notAuthenticated';
						} else {
							alert('Erro ao excluir usuário. Tente novamente.');
						}
					}
				}
				deleteButton.className = 'btn btn-danger';
				deleteButton.textContent = 'Excluir';
				actionsCell.appendChild(deleteButton);

				row.appendChild(actionsCell);				
				tbody.appendChild(row);
			});
		}
		
		table.appendChild(tbody);
		tableContainer.appendChild(table);
		
		// Replace any existing table or append to users section
		const existingTable = usersSection.querySelector('.users-table-container');
		if (existingTable) {
			existingTable.replaceWith(tableContainer);
		} else {
			usersSection.appendChild(tableContainer);
		}
		
	} catch (error) {
		console.error('Error fetching users:', error);
		// Show error message in the users section
		const usersSection = document.querySelector('.users-section');
		if (usersSection) {
			const errorDiv = document.createElement('div');
			errorDiv.className = 'error-message';
			errorDiv.textContent = 'Erro ao carregar usuários. Tente novamente.';
			usersSection.appendChild(errorDiv);
		}
	}
}

// Scrapper functions
function showCredentialsModal() {
	const modal = document.getElementById('credentials-modal');
	modal.style.display = 'block';
	
	// Focus on username field
	setTimeout(() => {
		document.getElementById('cagr-username').focus();
	}, 100);
}

function hideCredentialsModal() {
	const modal = document.getElementById('credentials-modal');
	modal.style.display = 'none';
	
	// Clear form
	document.getElementById('credentials-form').reset();
}

// Close modal when clicking outside
window.addEventListener('click', (event) => {
	const modal = document.getElementById('credentials-modal');
	if (event.target === modal) {
		hideCredentialsModal();
	}
});

async function executeScrapper() {
	const form = document.getElementById('credentials-form');
	const formData = new FormData(form);
	const submitButton = form.querySelector('button[type="submit"]');
	const originalText = submitButton.textContent;
	
	try {
		submitButton.disabled = true;
		submitButton.textContent = 'Executando...';
		
		const credentials = {
			cagrUsername: formData.get('cagrUsername'),
			cagrPassword: formData.get('cagrPassword')
		};
		
		const response = await fetch('/api/admin/scrapper/execute', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(credentials),
			credentials: 'same-origin'
		});
		
		if (response.status === 200) {
			const message = await response.text();
			alert('Sucesso: ' + message);
			hideCredentialsModal();
			// Refresh status after starting scrapper
			setTimeout(refreshScrapperStatus, 1000);
		} else if (response.status === 400) {
			const error = await response.text();
			alert('Erro: ' + error);
		} else if (response.status === 409) {
			const error = await response.text();
			alert('Aviso: ' + error);
		} else if (response.status === 403) {
			alert('Erro: Acesso negado. Você precisa ser administrador.');
		} else if (response.status === 401) {
			alert('Sessão expirada. Por favor refaça login.');
			document.location.href = '/login?error=notAuthenticated';
		} else {
			const error = await response.text();
			alert('Erro: ' + error);
		}
	} catch (error) {
		console.error('Error executing scrapper:', error);
		alert('Erro ao executar scrapper. Tente novamente.');
	} finally {
		submitButton.disabled = false;
		submitButton.textContent = originalText;
	}
}

async function refreshScrapperStatus() {
	const statusDiv = document.getElementById('scrapper-status');
	
	// Show loading
	statusDiv.innerHTML = `
		<div class="loading-overlay">
			<div class="spinner"></div>
			<div class="loading-overlay-text">Carregando status...</div>
		</div>
	`;
	
	try {
		const response = await fetch('/api/admin/scrapper/status', {
			credentials: 'same-origin'
		});
		
		if (!response.ok) {
			if (response.status === 403) {
				throw new Error('Acesso negado');
			} else if (response.status === 401) {
				document.location.href = '/login?error=notAuthenticated';
				return;
			}
			throw new Error('Erro ao obter status');
		}
		
		const status = await response.json();
		displayScrapperStatus(status);
		
	} catch (error) {
		console.error('Error fetching scrapper status:', error);
		statusDiv.innerHTML = `
			<div class="status-error">
				<span class="error-icon">⚠️</span>
				Erro ao carregar status: ${error.message}
			</div>
		`;
	}
}

function displayScrapperStatus(status) {
	const statusDiv = document.getElementById('scrapper-status');
	
	// Format dates
	const formatDate = (dateString) => {
		if (!dateString) return 'Nunca';
		const date = new Date(dateString);
		return date.toLocaleString('pt-BR');
	};
	
	const executando = status.executando ? 'Sim' : 'Não';
	const statusClass = status.executando ? 'status-running' : 'status-idle';
	const statusIcon = status.executando ? '🔄' : '✅';
	
	statusDiv.innerHTML = `
		<div class="status-card ${statusClass}">
			<div class="status-header">
				<span class="status-icon">${statusIcon}</span>
				<h3>Status do Scrapper</h3>
			</div>
			
			<div class="status-details">
				<div class="status-item">
					<label>Em execução:</label>
					<span class="status-value ${status.executando ? 'running' : 'idle'}">${executando}</span>
				</div>
				
				<div class="status-item">
					<label>Última execução:</label>
					<span class="status-value">${formatDate(status.ultimaExecucao)}</span>
				</div>
				
				<div class="status-item">
					<label>Último sucesso:</label>
					<span class="status-value">${formatDate(status.ultimoSucesso)}</span>
				</div>
				
				<div class="status-item">
					<label>Disciplinas capturadas:</label>
					<span class="status-value">${status.disciplinasCapturadas || 0}</span>
				</div>
				
				<div class="status-item">
					<label>Professores capturados:</label>
					<span class="status-value">${status.professoresCapturados || 0}</span>
				</div>
				
				<div class="status-item">
					<label>Último administrador:</label>
					<span class="status-value">${status.ultimoAdministrador || 'N/A'}</span>
				</div>
				
				${status.ultimoErro ? `
				<div class="status-item error">
					<label>Último erro:</label>
					<span class="status-value error">${status.ultimoErro}</span>
				</div>
				` : ''}
			</div>
		</div>
	`;
}

// Call the functions on page load
window.addEventListener('DOMContentLoaded', () => {
	fetchAndDisplayUsers();
	refreshScrapperStatus();
	
	// Add form submit listener
	document.getElementById('credentials-form').addEventListener('submit', (event) => {
		event.preventDefault();
		executeScrapper();
	});
});

