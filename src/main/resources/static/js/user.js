// User page JavaScript

// Modal functions
function openPasswordModal() {
    document.getElementById('passwordModal').style.display = 'block';
}

function closePasswordModal() {
    document.getElementById('passwordModal').style.display = 'none';
}

function openUploadModal() {
    document.getElementById('uploadModal').style.display = 'block';
}

function closeUploadModal() {
    document.getElementById('uploadModal').style.display = 'none';
}

function openDeleteModal() {
    document.getElementById('deleteModal').style.display = 'block';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
    document.getElementById('deleteConfirmation').value = '';
}

async function confirmDelete() {
    const confirmation = document.getElementById('deleteConfirmation').value;
	 const errorMessage = document.getElementById('delete-error-message');
    if (confirmation === 'EXCLUIR') {
		const code = await httpPost("/api/deleteUser", JSON.stringify({}));
		if (code == 401) {
			alert("Sessão expirada. Por favor refaça login.");
			document.location.href = "/";
			return;
		} else if (code == 400) {
			errorMessage.textContent = 'Erro ao excluir a conta. Tente novamente.';
			setTimeout(() => {
				errorMessage.textContent = '';
			}, 3000);
			return;
		} else if (code == 500) {
			errorMessage.textContent = 'Erro no servidor. Tente novamente mais tarde.';
			setTimeout(() => {
				errorMessage.textContent = '';
			}, 3000);
			return;
		}  
		alert('Conta excluída com sucesso.');
		document.location.href = "/";
		closeDeleteModal();
    } else {
        errorMessage.textContent = 'Digite "EXCLUIR" para confirmar a exclusão da conta';
        setTimeout(() => {
            errorMessage.textContent = '';
        }, 3000);
    }
}

// Close modals when clicking outside
window.onclick = function(event) {
    const modals = ['passwordModal', 'uploadModal', 'deleteModal'];
    modals.forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

async function httpPost(url, body) {
    const res = await fetch(url, {
        method: "POST",
        body: body,
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    })

    return res.status;
}

// Reset password form
function resetPasswordForm() {
    document.getElementById('currentPassword').value = "";
    document.getElementById('newPassword').value = "";
    document.getElementById('confirmNewPassword').value = "";
    document.getElementById('error-message').value = "";
}

function validatePassword(password) {
	// pelo menos 8 caracteres, um caixa alta, um caixa baixa, um número e um símbolo 
	let t = password.length;
	if(t < 8) return false;
	let hasUpper = false;
	let hasLower = false;
	let hasNumber = false;
	let hasSymbol = false;
	for(let i = 0; i<t; i++){
		let e = password[i];
		if(e >= 'A' && e <= 'Z') hasUpper=true;
		else if(e >= 'a' && e <= 'z') hasLower=true;
		else if(e >= '0' && e <= '9') hasNumber=true;
		else hasSymbol=true;
	};
	return hasUpper && hasLower && hasNumber && hasSymbol;
}


document.addEventListener('DOMContentLoaded', function() {
	const newPassword = document.getElementById('newPassword');
	const confirmNewPassword = document.getElementById('confirmNewPassword');
	let backendError = document.getElementById('backend-error-message');

	if(backendError){
		setTimeout(() => {
			backendError.textContent = '';
		}, 3000);
	}

	newPassword.addEventListener('blur', function() {
		if (!validatePassword(newPassword.value)) {
			document.getElementById('new-password-error-message').textContent = 'Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um símbolo.';
		} else {
			document.getElementById('new-password-error-message').textContent = '';
		}
	});

	confirmNewPassword.addEventListener('blur', function() {
		if (confirmNewPassword.value !== newPassword.value) {
			document.getElementById('confirm-password-error-message').textContent = 'As senhas não coincidem';
		} else {
			document.getElementById('confirm-password-error-message').textContent = '';
		}
	});
});

async function changePassword() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmNewPassword = document.getElementById('confirmNewPassword').value;
    const errorMessage = document.getElementById('error-message');
	 
	 document.getElementById('current-password-error-message').textContent = '';
	 document.getElementById('new-password-error-message').textContent = '';
	 document.getElementById('confirm-password-error-message').textContent = '';
	 errorMessage.textContent = '';

    if (newPassword && newPassword !== confirmNewPassword) {
        errorMessage.textContent = 'As senhas não coincidem';
		  setTimeout(() => {
				document.getElementById('error-message').textContent = '';
		  }, 3000);
		  return;
    }
    
    if (!validatePassword(newPassword)) {
        errorMessage.textContent = 'A nova senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um símbolo.';
		  setTimeout(() => {
			document.getElementById('error-message').textContent = '';
		  }, 3000);
		  return;
    }
    
    const code = await httpPost("/api/changePassword", JSON.stringify(
        {"currentPassword": currentPassword, "newPassword": newPassword}));
    
    if (code == 400){
		errorMessage.textContent = 'Senha atual incorreta.';
	} else if (code == 401) {
        alert("Sessão expirada. Por favor refaça login.");
        document.location.href = "/";
    } else {
        alert("Senha alterada com sucesso.")
        resetPasswordForm();
        closePasswordModal();
    }
}


document.addEventListener('DOMContentLoaded', async function() {
	// carrega as informações do usuário
    try {
		 const res = await fetch('/api/me', { credentials: 'same-origin' });
		 if (res.status === 401) {
			 window.location.href = '/login?error=notAuthenticated';
			 return;
        }
        if (!res.ok) {
			  console.error('Failed to load user', res.status);
			  return;
			}
        const user = await res.json();

        const name = document.getElementById('user-name');
        const email = document.getElementById('user-email');
        const matricula = document.getElementById('user-matricula');
        const curso = document.getElementById('user-curso');
        const initials = document.getElementById('user-initials');

        if (name) name.textContent = user.nome || '';
        if (email) email.textContent = user.email || '';
        if (matricula) matricula.textContent = user.matricula || '';
        if (curso) curso.textContent = user.curso || '';

        // Set initials from name
        if (initials) {
            var Words = user.nome.split(" ");
				var init = (Words[0][0].toUpperCase() || '') + (Words[Words.length-1][0].toUpperCase() || '');
            initials.textContent = init || 'U';
        }
    } catch (e) {
        console.error('Error loading user:', e);
    }
});