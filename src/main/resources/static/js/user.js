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

function confirmDelete() {
    const confirmation = document.getElementById('deleteConfirmation').value;
    if (confirmation === 'EXCLUIR') {
        // TODO: Implement account deletion
        alert('Funcionalidade de exclusão será implementada no backend');
        closeDeleteModal();
    } else {
        alert('Digite "EXCLUIR" para confirmar a exclusão da conta');
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

// Form validation
document.addEventListener('DOMContentLoaded', function() {
    // Password change form validation
    const passwordForm = document.querySelector('#passwordModal .modal-form');
    if (passwordForm) {
        passwordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmNewPassword = document.getElementById('confirmNewPassword').value;
            
            if (newPassword !== confirmNewPassword) {
                alert('As senhas não coincidem');
                return;
            }
            
            if (newPassword.length < 6) {
                alert('A nova senha deve ter pelo menos 6 caracteres');
                return;
            }
            
            // TODO: Implement password change
            alert('Funcionalidade de alteração de senha será implementada no backend');
            closePasswordModal();
        });
    }
    
    // File upload form validation
    const uploadForm = document.querySelector('#uploadModal .modal-form');
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const fileInput = document.getElementById('newPdf');
            if (!fileInput.files.length) {
                alert('Selecione um arquivo PDF');
                return;
            }
            
            const file = fileInput.files[0];
            if (file.type !== 'application/pdf') {
                alert('Selecione apenas arquivos PDF');
                return;
            }
            
            // TODO: Implement file upload
            alert('Funcionalidade de upload será implementada no backend');
            closeUploadModal();
        });
    }
});

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