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
					
					const code = await httpPost("/api/admin/toggle-admin", JSON.stringify({ email: user.email }));
					if (code === 200) {
						// Refresh the user list to reflect changes
						typeSpan.className = !user.admin ? 'user-type admin' : 'user-type user';
						typeSpan.textContent = !user.admin ? 'Admin' : 'Usuário';
						toggleButton.textContent = !user.admin ? 'Remover Admin' : 'Tornar Admin';
						user.admin = !user.admin; // Update local state
						// fetchAndDisplayUsers();
					} else if (code === 400) {
						alert('Erro: Não é possível alterar o status de admin do próprio usuário.');
					} else if (code === 401) {
						alert('Sessão expirada. Por favor refaça login.');
						document.location.href = '/login?error=notAuthenticated';
					} else {
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
						const code = await httpPost("/api/admin/delete-user", JSON.stringify({ email: user.email }));
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


// Call the function on page load
window.addEventListener('DOMContentLoaded', fetchAndDisplayUsers);

