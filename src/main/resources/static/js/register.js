function validateEmail(email) {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return emailRegex.test(email);
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
	const fileInput = document.getElementById('pdf');
	const fileUploadText = document.querySelector('.file-upload-text span:last-child');
   
	const emailInput = document.getElementById('email');
	const passwordInput = document.getElementById('password');
	const confirmPasswordInput = document.getElementById('confirmPassword');
	let backendError = document.getElementById('backend-error-message');
	if(backendError){
		setTimeout(() => {
			backendError.textContent = '';
		}, 3000);
	}

	emailInput.addEventListener('blur', function() {
		if(!validateEmail(emailInput.value)){
			document.getElementById('email-error-message').textContent = 'Email inválido.';
		} else {
			document.getElementById('email-error-message').textContent = '';
		}
	});

	passwordInput.addEventListener('input', function() {
		if (passwordInput.value.length >= 8 && !validatePassword(passwordInput.value)) {
			document.getElementById('password-error-message').textContent = 'Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um símbolo.';
		} else if(passwordInput.value.length >= 8 && validatePassword(passwordInput.value)){
			document.getElementById('password-error-message').textContent = '';
		}
		if(confirmPasswordInput.value.length >= 8 && confirmPasswordInput.value !== passwordInput.value){
			document.getElementById('password-confirm-error-message').textContent = 'As senhas não coincidem';
		} else if(confirmPasswordInput.value.length >= 8 && confirmPasswordInput.value === passwordInput.value){
			document.getElementById('password-confirm-error-message').textContent = '';
		}
	});

	passwordInput.addEventListener('blur', function() {
		if (!validatePassword(passwordInput.value)) {
			document.getElementById('password-error-message').textContent = 'Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um símbolo.';
		} else if(validatePassword(passwordInput.value)){
			document.getElementById('password-error-message').textContent = '';
		}
		if(confirmPasswordInput.value !== passwordInput.value){
			document.getElementById('password-confirm-error-message').textContent = 'As senhas não coincidem';
		} else {
			document.getElementById('password-confirm-error-message').textContent = '';
		}
	});

	confirmPasswordInput.addEventListener('input', function() {
		if (confirmPasswordInput.value.length >= 8 && confirmPasswordInput.value !== passwordInput.value) {
			document.getElementById('password-confirm-error-message').textContent = 'As senhas não coincidem';
		} else if(confirmPasswordInput.value.length >= 8 && confirmPasswordInput.value === passwordInput.value){
			document.getElementById('password-confirm-error-message').textContent = '';
		}
	});

	confirmPasswordInput.addEventListener('blur', function() {
		if (confirmPasswordInput.value !== passwordInput.value) {
			document.getElementById('password-confirm-error-message').textContent = 'As senhas não coincidem';
		} else {
			document.getElementById('password-confirm-error-message').textContent = '';
		}
	});
	
	fileInput.addEventListener('change', function(e) {
		if(backendError) backendError.textContent = '';
		const file = e.target.files[0];
		if (file) {
			// Update the text to show the selected file name
			fileUploadText.textContent = `Arquivo selecionado: ${file.name}`;
			// Optional: Add a class for styling (e.g., to change color)
			fileUploadText.parentElement.classList.add('file-selected');
		} else {
			// Reset if no file is selected
			fileUploadText.textContent = 'Selecione seu histórico acadêmico em PDF';
			fileUploadText.parentElement.classList.remove('file-selected');
		}
	});

	function checkInitialFile(){
		if(fileInput.files && fileInput.files[0]){
			const file = fileInput.files[0];
			fileUploadText.textContent = `Arquivo selecionado: ${file.name}`;
			fileUploadText.parentElement.classList.add('file-selected');
		}
	}
	checkInitialFile();
});


function validateAndSubmit() {
	let isValid = true;
	let errorMessage = document.getElementById('error-message');
	const form = document.querySelector('form'); // Assuming there's a form element
	const fileInput = document.getElementById('pdf');
   
	const emailInput = document.getElementById('email');
	const passwordInput = document.getElementById('password');
	const confirmPasswordInput = document.getElementById('confirmPassword');
	
	// Clear previous error message
	errorMessage.textContent = '';
	
	// Check all validations separately (not else if chain)
	if (!validateEmail(emailInput.value)) {
		errorMessage.textContent = 'Email inválido.';
		isValid = false;
	}
	if (!validatePassword(passwordInput.value)) {
		errorMessage.textContent = 'Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula, um número e um símbolo.';
		isValid = false;
	}
	if (confirmPasswordInput.value !== passwordInput.value) {
		errorMessage.textContent = 'As senhas não coincidem';
		isValid = false;
	}
	if (!fileInput.files || !fileInput.files[0]) {
		errorMessage.textContent = 'Selecione um arquivo pdf válido.';
		isValid = false;
	} else {
		const file = fileInput.files[0];
		const maxSizeInBytes = 1 * 1024 * 1024; // 1MB
		if (file.size > maxSizeInBytes) {
			errorMessage.textContent = 'O arquivo excede o tamanho máximo de 1MB.';
			isValid = false;
		}
	}
	console.log("Form is:" + (isValid ? "Valid" : "Not Valid"));
	// alert(fileInput.files[0].name + (isValid ? "Valid" : "Not Valid"));

	if (!isValid) {
		document.getElementById('email-error-message').textContent = '';
		document.getElementById('password-error-message').textContent = '';
		document.getElementById('password-confirm-error-message').textContent = '';
		setTimeout(() => {
			document.getElementById('error-message').textContent = '';
		}, 3000);
	} else {
		console.log("Submitting form...");
		form.submit();
	}
}