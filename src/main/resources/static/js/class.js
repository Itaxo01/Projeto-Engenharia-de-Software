/**
 * Página da Disciplina - JavaScript
 */

console.log('✅ class.js carregado');

let professorSelecionado = null;
let selectedFiles = [];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
let isAdmin = false; // Flag de admin
let editingCommentId = null; // Track if we're editing a comment
let editingCommentData = null; // Store original comment data for editing

document.addEventListener('DOMContentLoaded', function() {
    if (typeof AVALIACOES_DATA === 'undefined') return;
    if (typeof COMENTARIOS_DATA === 'undefined') {
        console.error('COMENTARIOS_DATA não está definido!');
        return;
    }
    
    // Obter email do usuário logado e status de admin
    isAdmin = typeof IS_ADMIN !== 'undefined' ? IS_ADMIN : false;
    
    console.log('📊 Dados carregados:', {
        avaliacoes: AVALIACOES_DATA.length,
        comentarios: COMENTARIOS_DATA.length,
        professores: PROFESSORES_DATA?.length || 0,
        isAdmin: isAdmin
    });

	 AVALIACOES_DATA.forEach(a => {
		console.log('Avaliação:', a);
	 });

	 COMENTARIOS_DATA.forEach(c => {
		console.log('Comentário:', c);
	 });
    
    // Separar avaliações
    const avaliacoesDisciplina = AVALIACOES_DATA.filter(a => !a.professorId);
    const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
    
    // Atualizar disciplina com comentários
    const statsDisciplina = calcularStats(avaliacoesDisciplina);
    const comentariosDisciplina = COMENTARIOS_DATA.filter(c => !c.professorId);
    atualizarDisciplina(statsDisciplina, comentariosDisciplina);
    
    // Atualizar professores e adicionar event listeners
    if (typeof PROFESSORES_DATA !== 'undefined') {
        console.log('👨‍🏫 Professores:', PROFESSORES_DATA);
        
        PROFESSORES_DATA.forEach((prof, index) => {
            console.log(`Professor ${index}:`, prof);
            
            // Identificar qual propriedade contém o ID (pode ser 'id', 'professorId', ou outra)
            const profId = prof.id || prof.professorId || prof.ID || index;
            const profNome = prof.nome || prof.name || 'Professor';
            
            const avaliacoesProf = avaliacoesProfessores.filter(a => 
                String(a.professorId) === String(profId)
            );
            const stats = calcularStats(avaliacoesProf);
            atualizarProfessor(index, stats);
            
            // Adicionar click listener
            const professorItem = document.querySelectorAll('.professor-item')[index];
            if (professorItem) {
                professorItem.addEventListener('click', () => {
                    // Toggle: se já está selecionado, desseleciona (comparar como string)
                    if (String(professorSelecionado) === String(profId)) {
                        console.log('Desselecionando professor:', profId);
                        deselecionarProfessor();
                    } else {
                        console.log('Alternando para professor:', profId, profNome);
                        // ✅ FIX: Recalcular avaliações do professor dinamicamente
                        selecionarProfessor(profId, profNome);
                    }
                });
            }
        });
    }
    
    // Event listener no header da disciplina para voltar às avaliações da disciplina
    const disciplineHeader = document.querySelector('.discipline-header');
    if (disciplineHeader) {
        disciplineHeader.style.cursor = 'pointer';
        disciplineHeader.addEventListener('click', () => {
            deselecionarProfessor();
        });
    }
    
    // Setup comment editor
    setupCommentEditor();
    
    // ✅ Setup interactive stars for discipline and professors
    setupInteractiveStars();
});

// ✅ Make stars clickable for rating submission
function setupInteractiveStars() {
    // Discipline stars
    const disciplineStars = document.querySelector('.discipline-rating-stars');
    if (disciplineStars) {
        makeStarsInteractive(disciplineStars, null); // null = disciplina (sem professor)
    }
    
    // Professor stars
    const professorItems = document.querySelectorAll('.professor-item');
    professorItems.forEach((item, index) => {
        const starsContainer = item.querySelector('.rating-stars');
        if (starsContainer && PROFESSORES_DATA && PROFESSORES_DATA[index]) {
            const prof = PROFESSORES_DATA[index];
            const profId = prof.id || prof.professorId || prof.ID || index;
            makeStarsInteractive(starsContainer, profId);
        }
    });
}

function makeStarsInteractive(starsContainer, professorId) {
    // ✅ FIX: Remove existing listeners by cloning to prevent duplicate submissions
    starsContainer.querySelectorAll('.star').forEach((oldStar, index) => {
        const newStar = oldStar.cloneNode(true);
        oldStar.parentNode.replaceChild(newStar, oldStar);
    });
    
    // Re-query stars after cloning
    const freshStars = starsContainer.querySelectorAll('.star');
    
    // Get initial average rating to display
    const avgRating = parseFloat(starsContainer.closest('.discipline-rating, .professor-rating')
        ?.querySelector('.rating-value, .rating-score')?.textContent) || 0;
    
    // Check if current user has voted
    const userRating = getUserCurrentRating(professorId);
    const hasUserVoted = userRating !== null;
    
    // Display the average rating on load
    if (avgRating > 0) {
        freshStars.forEach((star, index) => {
            star.classList.remove('filled', 'half', 'hover', 'user-voted', 'half-user-voted');
            if (avgRating >= index + 1) {
                star.classList.add('filled');
                // Add user-voted class if user has voted
                if (hasUserVoted) {
                    star.classList.add('user-voted');
                }
            } else if (avgRating >= index + 0.5) {
                // Add appropriate half class based on whether user voted
                if (hasUserVoted) {
                    star.classList.add('half-user-voted');
                } else {
                    star.classList.add('half');
                }
            }
        });
    }
    
    // ✅ REMOVED: Direct star clicking - now only visual display
    // Stars are no longer clickable; users must use the "Adicionar avaliação" link
    freshStars.forEach(star => {
        star.style.cursor = 'default'; // Change cursor to indicate not clickable
    });
}

function highlightStars(stars, rating) {
    stars.forEach((star, index) => {
        // Remove all state classes first
        star.classList.remove('hover', 'half', 'filled', 'user-voted', 'half-user-voted');
        
        if (index < rating) {
            star.classList.add('hover');
        }
    });
}

function clearStarsHighlight(stars) {
    stars.forEach(star => {
        star.classList.remove('hover', 'half', 'filled', 'half-user-voted', 'user-voted');
    });
}

function getUserCurrentRating(professorId) {
    
    const avaliacao = AVALIACOES_DATA.find(a => {
        return a.isOwner && (String(a.professorId || '') === String(professorId || ''));
    });

	 
	 console.log('Avaliação do usuário para professorId', professorId, ':', avaliacao?.nota);
    return avaliacao?.nota || null;
}

// Calcular média e total
function calcularStats(avaliacoes) {
    const comNota = avaliacoes.filter(a => a.nota > 0);
    if (comNota.length === 0) return { media: 0, total: 0 };
    
    const soma = comNota.reduce((acc, a) => acc + a.nota, 0);
    return { media: soma / comNota.length, total: comNota.length };
}

// Atualizar header da disciplina
function atualizarDisciplina(stats, comentarios) {
    document.querySelector('.discipline-rating .rating-score').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
    const ratingCountElement = document.querySelector('.discipline-rating .rating-count');
    ratingCountElement.textContent = 
        `${stats.total} ${stats.total === 1 ? 'avaliação' : 'avaliações'}`;
    
    // Add "remove rating" link if user has voted
    addRemoveRatingLink(ratingCountElement, null);
    
    preencherEstrelas(document.querySelector('.discipline-rating .rating-stars'), stats.media);
    
    // Mostrar comentários da disciplina
    mostrarComentarios(comentarios, null);
}

// Selecionar professor
function selecionarProfessor(professorId, professorNome) {
    console.log('Selecionando professor:', professorId, 'Atual:', professorSelecionado);
    
    // Atualizar professor selecionado
    professorSelecionado = professorId;
    
    // ✅ FIX: Recalcular avaliações do professor dinamicamente do array global
    const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
    const avaliacoes = avaliacoesProfessores.filter(a => 
        String(a.professorId) === String(professorId)
    );
    
    console.log(`📊 Avaliações do professor ${professorNome}:`, avaliacoes.length);
    
    // Remover seleção anterior de TODOS os professores
    document.querySelectorAll('.professor-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Encontrar e selecionar o professor correto
    const professorItems = Array.from(document.querySelectorAll('.professor-item'));
    professorItems.forEach((item, idx) => {
        // Obter ID do professor usando a mesma lógica de fallback
        const prof = PROFESSORES_DATA[idx];
        if (prof) {
            const profId = prof.id || prof.professorId || prof.ID || idx;
            if (String(profId) === String(professorId)) {
                item.classList.add('selected');
                console.log('✓ Professor selecionado no índice:', idx, 'ID:', profId);
            }
        }
    });
    
    // Atualizar título da seção de avaliações
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = `Avaliações - ${professorNome}`;
    }
    
    // ✅ Mostrar comentários do professor usando COMENTARIOS_DATA
    const comentariosProfessor = COMENTARIOS_DATA.filter(c => 
        String(c.professorId) === String(professorId)
    );
    mostrarComentarios(comentariosProfessor, professorNome);
}

// Desselecionar professor
function deselecionarProfessor() {
    console.log('Desselecionando professor');
    professorSelecionado = null;
    
    // Remover todas as seleções
    document.querySelectorAll('.professor-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Restaurar título original
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = 'Avaliações';
    }
    
    // ✅ Mostrar comentários da disciplina novamente usando COMENTARIOS_DATA
    const comentariosDisciplina = COMENTARIOS_DATA.filter(c => !c.professorId);
    mostrarComentarios(comentariosDisciplina, null);
}

// Atualizar professor específico
function atualizarProfessor(index, stats) {
    const professorItem = document.querySelectorAll('.professor-item')[index];
    if (!professorItem) return;
    
    professorItem.querySelector('.rating-value').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
    const ratingCountElement = professorItem.querySelector('.rating-count');
    ratingCountElement.textContent = 
        `${stats.total} ${stats.total === 1 ? 'avaliação' : 'avaliações'}`;
    
    // Add "remove rating" link if user has voted for this professor
    const prof = PROFESSORES_DATA[index];
    const profId = prof?.id || prof?.professorId || prof?.ID || index;
    addRemoveRatingLink(ratingCountElement, profId);
    
    preencherEstrelas(professorItem.querySelector('.rating-stars'), stats.media);
}

// Preencher estrelas
function preencherEstrelas(container, nota) {
    const stars = container.querySelectorAll('.star');
    stars.forEach((star, i) => {
        star.classList.remove('filled', 'half');
        if (nota >= i + 1) star.classList.add('filled');
        else if (nota >= i + 0.5) star.classList.add('half');
    });
}

// Mostrar comentários
function mostrarComentarios(comentarios, professorNome) {
    const lista = document.querySelector('.reviews-list');
    
    if (comentarios.length === 0) {
        const entidade = professorNome || 'esta disciplina';
        lista.innerHTML = `<div class="no-reviews"><p>Ainda não há avaliações para ${entidade}.</p><p>Seja o primeiro a avaliar!</p></div>`;
        return;
    }
    
    lista.innerHTML = comentarios.map(comentario => {
        const isOwner = comentario.isOwner || false;
        const hasVoted = comentario.hasVoted; // 1 (upvote), -1 (downvote), 0 (no vote)
        
        // Classes para destacar botões de voto
        const upvoteClass = hasVoted === 1 ? 'voted' : '';
        const downvoteClass = hasVoted === -1 ? 'voted' : '';
        
        // ✅ Show delete button if user is owner OR admin
        const canDelete = isOwner || isAdmin;
        
        // ✅ Render attached files
        const arquivosHTML = comentario.arquivos && comentario.arquivos.length > 0 
            ? `<div class="comment-attachments">${renderArquivos(comentario.arquivos)}</div>`
            : '';
        
		return `
		<div class="review-card" data-comment-id="${comentario.id}">
			 <div class="review-header">
			<div class="reviewer-info">
				 <div class="reviewer-avatar">${String.fromCharCode(65 + (comentario.id % 26))}</div>
				 <span class="reviewer-date">${formatarData(comentario.createdAt)}</span>
			</div>
			 </div>
			 <div class="review-content">${escapeHtml(comentario.texto)}</div>
			 
			 <!-- Arquivos Anexados -->
			 ${arquivosHTML}
			 
			 <!-- Botões de Ação -->
			 <div class="review-actions">
			<button class="review-action-btn upvote-btn ${upvoteClass}" onclick="voteComment(${comentario.id}, true)">
				 <span class="vote-icon">👍</span>
				 <span class="vote-count">${comentario.upVotes || 0}</span>
			</button>
			<button class="review-action-btn downvote-btn ${downvoteClass}" onclick="voteComment(${comentario.id}, false)">
				 <span class="vote-icon">👎</span>
				 <span class="vote-count">${comentario.downVotes || 0}</span>
			</button>
			<button class="review-action-btn reply-btn" onclick="replyToComment(${comentario.id})">
				 <span class="vote-icon">💬</span>
				 <span>Responder</span>
			</button>
			${isOwner ? `
			<button class="review-action-btn edit-btn" onclick="editComment(${comentario.id})">
				 <span class="vote-icon">✏️</span>
				 <span>Editar</span>
			</button>
			` : ''}
			${canDelete ? `
			<button class="review-action-btn delete-btn" onclick="deleteComment(${comentario.id})">
				 <span class="vote-icon">🗑️</span>
				 <span>Deletar</span>
			</button>
			` : ''}
			 </div>
		</div>
		`;
    }).join('');
}

// Formatar data
function formatarData(data) {
    if (!data) return 'Data desconhecida';
    const d = new Date(data);
    const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
    return `${meses[d.getMonth()]} ${d.getFullYear()}`;
}

/**
 * Render attached files (images and documents)
 */
function renderArquivos(arquivos) {
    if (!arquivos || arquivos.length === 0) return '';
    
    return arquivos.map(arquivo => {
        const isImage = arquivo.tipoMime && arquivo.tipoMime.startsWith('image/');
        const isPdf = arquivo.tipoMime === 'application/pdf';
        
        if (isImage) {
            // Image preview with click to enlarge
            return `
                <div class="attachment-item attachment-image">
                    <img src="/api/arquivos/${arquivo.id}" 
                         alt="${escapeHtml(arquivo.nomeOriginal)}"
                         onclick="openImageModal('/api/arquivos/${arquivo.id}', '${escapeHtml(arquivo.nomeOriginal)}')"
                         loading="lazy">
                    <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                </div>
            `;
        } else if (isPdf) {
            // PDF preview with click to view/download
            return `
                <div class="attachment-item attachment-pdf">
                    <div class="pdf-icon">📄</div>
                    <div class="attachment-info">
                        <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                        <div class="attachment-size">${formatFileSize(arquivo.tamanho)}</div>
                    </div>
                    <div class="attachment-actions">
                        <a href="/api/arquivos/${arquivo.id}" target="_blank" class="btn-view">Ver</a>
                        <a href="/api/arquivos/${arquivo.id}?download=true" class="btn-download">Baixar</a>
                    </div>
                </div>
            `;
        } else {
            // Other documents (Word, Excel, etc.) - download only
            const icon = getFileIcon(arquivo.nomeOriginal);
            return `
                <div class="attachment-item attachment-doc">
                    <div class="doc-icon">${icon}</div>
                    <div class="attachment-info">
                        <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                        <div class="attachment-size">${formatFileSize(arquivo.tamanho)}</div>
                    </div>
                    <a href="/api/arquivos/${arquivo.id}?download=true" class="btn-download">Baixar</a>
                </div>
            `;
        }
    }).join('');
}

/**
 * Open image in modal for full view
 */
function openImageModal(imageUrl, imageName) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('imageModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'imageModal';
        modal.className = 'image-modal';
        modal.innerHTML = `
            <div class="image-modal-content">
                <span class="image-modal-close" onclick="closeImageModal()">&times;</span>
                <img id="modalImage" src="" alt="">
                <div id="modalImageName" class="modal-image-name"></div>
            </div>
        `;
        document.body.appendChild(modal);
        
        // Close on click outside
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeImageModal();
            }
        });
    }
    
    // Set image and show modal
    document.getElementById('modalImage').src = imageUrl;
    document.getElementById('modalImageName').textContent = imageName;
    modal.style.display = 'flex';
}

/**
 * Close image modal
 */
function closeImageModal() {
    const modal = document.getElementById('imageModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// ============================================
// COMMENT EDITOR FUNCTIONS
// ============================================

function setupCommentEditor() {
    const textarea = document.getElementById('commentText');
    const submitBtn = document.getElementById('submitBtn');
    
    if (textarea && submitBtn) {
        textarea.addEventListener('input', function() {
            updateSubmitButton();
        });
    }
}

function updateSubmitButton() {
    const textarea = document.getElementById('commentText');	
    const submitBtn = document.getElementById('submitBtn');
    const hasText = textarea.value.trim().length > 0;
    // ✅ Comentário não precisa mais de rating
    submitBtn.disabled = !hasText;
}

function toggleCommentEditor() {
    const editor = document.getElementById('commentEditor');
    const button = document.querySelector('.btn-add-review');
    
    if (editor.classList.contains('show')) {
        // Close editor
        editor.classList.remove('show');
        button.classList.remove('active');
        resetCommentEditor();
    } else {
        // Open editor
        editor.classList.add('show');
        button.classList.add('active');
        
        // Update subtitle based on current context (or edit mode)
        updateEditorSubtitle();
        
        // Focus on textarea
        setTimeout(() => {
            document.getElementById('commentText').focus();
        }, 100);
    }
}

function updateEditorSubtitle() {
    const subtitle = document.getElementById('editorSubtitle');
    
    // If editing, show edit message
    if (editingCommentId !== null) {
        subtitle.textContent = 'Editando comentário';
        return;
    }
    
    if (professorSelecionado !== null && PROFESSORES_DATA) {
        // Find professor name
        const prof = PROFESSORES_DATA.find(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorSelecionado);
        });
        
        const profNome = prof?.nome || prof?.name || 'este professor';
        subtitle.textContent = `Comentando sobre ${profNome}`;
    } else {
        subtitle.textContent = 'Comentando sobre a disciplina';
    }
}

// ✅ Removed selectRating, hoverRating, unhoverRating - no longer needed in comment modal

function resetCommentEditor() {
    // Clear textarea
    document.getElementById('commentText').value = '';
    
    // Clear files
    selectedFiles = [];
    document.getElementById('fileInput').value = '';
    renderFileList();
    
    // Clear edit mode
    editingCommentId = null;
    editingCommentData = null;
    
    // Disable submit button
    document.getElementById('submitBtn').disabled = true;
}

// ============================================
// FILE UPLOAD FUNCTIONS
// ============================================

function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    const fileList = document.getElementById('fileList');
    
    // Allowed file types for security
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
    const allowedMimeTypes = [
        'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp',
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'text/plain'
    ];
    
    // Clear error messages
    fileList.querySelectorAll('.file-error').forEach(el => el.remove());
    
    files.forEach(file => {
        // Check file size
        if (file.size > MAX_FILE_SIZE) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Arquivo "${file.name}" excede o tamanho máximo de 5MB`;
            fileList.appendChild(error);
            return;
        }
        
        // Check file extension
        const extension = '.' + file.name.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(extension)) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Tipo de arquivo não permitido: "${file.name}". Apenas imagens (JPG, PNG, GIF, WebP) e documentos (PDF, DOC, DOCX, XLS, XLSX, TXT) são permitidos.`;
            fileList.appendChild(error);
            return;
        }
        
        // Check MIME type
        if (!allowedMimeTypes.includes(file.type)) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Tipo de arquivo não permitido: "${file.name}". Apenas imagens e documentos seguros são permitidos.`;
            fileList.appendChild(error);
            return;
        }
        
        // Check if file already exists
        const exists = selectedFiles.some(f => f.name === file.name && f.size === file.size);
        if (!exists) {
            selectedFiles.push(file);
        }
    });
    
    // Clear input to allow selecting the same file again
    event.target.value = '';
    
    renderFileList();
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    renderFileList();
}

function renderFileList() {
    const fileList = document.getElementById('fileList');
    const errors = fileList.querySelectorAll('.file-error');
    
    if (selectedFiles.length === 0 && errors.length === 0) {
        fileList.innerHTML = '';
        return;
    }
    
    const filesHTML = selectedFiles.map((file, index) => `
        <div class="file-item">
            <div class="file-item-info">
                <span class="file-item-icon">${getFileIcon(file.name)}</span>
                <span class="file-item-name">${file.name}</span>
                <span class="file-item-size">${formatFileSize(file.size)}</span>
            </div>
            <button type="button" class="file-item-remove" onclick="removeFile(${index})" title="Remover arquivo">×</button>
        </div>
    `).join('');
    
    // Preserve error messages
    const errorsHTML = Array.from(errors).map(el => el.outerHTML).join('');
    
    fileList.innerHTML = filesHTML + errorsHTML;
}

function getFileIcon(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    const icons = {
        'pdf': '📄',
        'doc': '📝',
        'docx': '📝',
        'jpg': '🖼️',
        'jpeg': '🖼️',
        'png': '🖼️',
        'gif': '🖼️'
    };
    return icons[ext] || '📎';
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// ============================================
// SUBMIT COMMENT
// ============================================

async function submitComment(event) {
    event.preventDefault();
    
    const texto = document.getElementById('commentText').value.trim();
    
    if (!texto) {
        alert('Por favor, escreva um comentário.');
        return;
    }
    
    // Check if we're editing or creating
    if (editingCommentId !== null) {
        // EDIT MODE
        await submitEditComment(editingCommentId, texto);
    } else {
        // CREATE MODE
        console.log('Submitting comment:', {
            texto,
            professorId: professorSelecionado,
            disciplinaId: typeof CLASS_ID !== 'undefined' ? CLASS_ID : null,
            files: selectedFiles.map(f => ({ name: f.name, size: f.size, type: f.type }))
        });
        
        // Prepare FormData for file upload
        const formData = new FormData();
        formData.append('texto', texto);
        formData.append('disciplinaId', typeof CLASS_ID !== 'undefined' ? CLASS_ID : '');
        if (professorSelecionado) {
            formData.append('professorId', professorSelecionado);
        } else {
            formData.append('professorId', "");
        }
        selectedFiles.forEach((file, index) => {
            formData.append(`files`, file);
        });
        
        try {
            const response = await fetch('/api/avaliacao/comentario', {
                method: 'POST',
                body: formData
            });
            if (!response.ok) {
                const errorText = await response.text();
                alert('Erro ao enviar comentário: ' + errorText);
                return;
            }
            const result = await response.json();
            console.log('Comentário enviado com sucesso:', result);
            
            // Reload page to show new comment
            window.location.reload();
            
        } catch (error) {
            console.error('Erro ao enviar comentário:', error);
            alert('Erro ao enviar comentário: ' + error.message);
            return;
        }
    }
}

/**
 * Submit edited comment
 */
async function submitEditComment(comentarioId, novoTexto) {
    console.log('Editing comment:', {
        comentarioId,
        novoTexto,
        files: selectedFiles.map(f => ({ name: f.name, size: f.size, type: f.type }))
    });
    
    // Prepare FormData
    const formData = new FormData();
    formData.append('novoTexto', novoTexto);
    
    // Add files (could be old files or new files)
    selectedFiles.forEach((file) => {
        formData.append('files', file);
    });
    
    try {
        const response = await fetch(`/api/comentarios/${comentarioId}/editar`, {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao editar comentário: ' + errorText);
            return;
        }
        
        const result = await response.json();
        console.log('Comentário editado com sucesso:', result);
        
        // Reload page to show edited comment
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao editar comentário:', error);
        alert('Erro ao editar comentário: ' + error.message);
    }
}

// ============================================
// VALIDATION & INTERACTION FUNCTIONS
// ============================================

/**
 * Add "remove rating" or "add rating" link below rating count
 */
function addRemoveRatingLink(ratingCountElement, professorId) {
    // Check if user has voted for this context
    const userRating = getUserCurrentRating(professorId);
    
    // Remove existing link if present
    const existingLink = ratingCountElement.parentElement.querySelector('.remove-rating-link, .add-rating-link');
    if (existingLink) {
        existingLink.remove();
    }
    
    if (userRating !== null) {
        // User has rated - show remove link
        const removeLink = document.createElement('div');
        removeLink.className = 'remove-rating-link';
        // ✅ FIX: Use quotes to preserve professorId as string (prevents "0882497234989588" from becoming 882497234989588)
        removeLink.innerHTML = `<span onclick="removeRating('${professorId}')">Remover minha avaliação</span>`;
        ratingCountElement.parentElement.appendChild(removeLink);
    } else {
        // User hasn't rated - show add link
        const addLink = document.createElement('div');
        addLink.className = 'add-rating-link';
        addLink.innerHTML = `<span onclick="openRatingModal('${professorId}')">Adicionar avaliação</span>`;
        ratingCountElement.parentElement.appendChild(addLink);
    }
}

/**
 * Remove user's rating
 */
async function removeRating(professorId = null) {
    
    if (!confirm('Tem certeza que deseja remover sua avaliação?')) {
        return;
    }
    
    try {
        const formData = new FormData();
        formData.append('disciplinaId', CLASS_ID);
        if (professorId) {
            formData.append('professorId', professorId);
        }
        
        const response = await fetch('/api/avaliacao/rating/delete', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao remover avaliação: ' + errorText);
            return;
        }
        
        console.log('Avaliação removida com sucesso');
        
        // Reload page to show updated ratings
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao remover avaliação:', error);
        alert('Erro ao remover avaliação: ' + error.message);
    }
}

/**
 * Open rating modal for user to select stars
 */
function openRatingModal(professorId = null) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('ratingModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'ratingModal';
        modal.className = 'rating-modal';
        modal.innerHTML = `
            <div class="rating-modal-content">
                <span class="rating-modal-close" onclick="closeRatingModal()">&times;</span>
                <h3 id="ratingModalTitle">Avaliar</h3>
                <p id="ratingModalSubtitle">Selecione sua nota:</p>
                <div class="rating-modal-stars" id="ratingModalStars">
                    <span class="modal-star" data-rating="1">★</span>
                    <span class="modal-star" data-rating="2">★</span>
                    <span class="modal-star" data-rating="3">★</span>
                    <span class="modal-star" data-rating="4">★</span>
                    <span class="modal-star" data-rating="5">★</span>
                </div>
                <div class="rating-modal-actions">
                    <button class="btn-modal-cancel" onclick="closeRatingModal()">Cancelar</button>
                    <button class="btn-modal-submit" id="btnModalSubmitRating" disabled onclick="submitModalRating()">Confirmar</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
        
        // Close on click outside
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeRatingModal();
            }
        });
    }
    
    // Store the professor ID for later use
    modal.dataset.professorId = professorId || '';
    
    // Update title based on context
    const titleElement = document.getElementById('ratingModalTitle');
    if (professorId !== null && professorId !== 'null' && PROFESSORES_DATA) {
        const prof = PROFESSORES_DATA.find(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorId);
        });
        const profNome = prof?.nome || prof?.name || 'Professor';
        titleElement.textContent = `Avaliar ${profNome}`;
    } else {
        titleElement.textContent = 'Avaliar Disciplina';
    }
    
    // Reset stars
    const modalStars = document.querySelectorAll('.modal-star');
    modalStars.forEach(star => {
        star.classList.remove('selected', 'hover');
        
        // Add hover and click events
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.dataset.rating);
            highlightModalStars(rating);
        });
        
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            selectModalRating(rating);
        });
    });
    
    // Reset on mouse leave
    const starsContainer = document.getElementById('ratingModalStars');
    starsContainer.addEventListener('mouseleave', function() {
        const selectedRating = modal.dataset.selectedRating;
        if (selectedRating) {
            highlightModalStars(parseInt(selectedRating));
        } else {
            clearModalStars();
        }
    });
    
    // Reset selection
    delete modal.dataset.selectedRating;
    document.getElementById('btnModalSubmitRating').disabled = true;
    
    // Show modal
    modal.style.display = 'flex';
}

/**
 * Close rating modal
 */
function closeRatingModal() {
    const modal = document.getElementById('ratingModal');
    if (modal) {
        modal.style.display = 'none';
        delete modal.dataset.selectedRating;
        delete modal.dataset.professorId;
        clearModalStars();
    }
}

/**
 * Highlight modal stars on hover
 */
function highlightModalStars(rating) {
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach((star, index) => {
        star.classList.remove('hover');
        if (index < rating) {
            star.classList.add('hover');
        }
    });
}

/**
 * Clear modal stars highlighting
 */
function clearModalStars() {
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach(star => {
        star.classList.remove('hover', 'selected');
    });
}

/**
 * Select a rating in the modal
 */
function selectModalRating(rating) {
    const modal = document.getElementById('ratingModal');
    modal.dataset.selectedRating = rating;
    
    // Update stars to show selection
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach((star, index) => {
        star.classList.remove('selected', 'hover');
        if (index < rating) {
            star.classList.add('selected');
        }
    });
    
    // Enable submit button
    document.getElementById('btnModalSubmitRating').disabled = false;
}

/**
 * Submit the rating from modal
 */
async function submitModalRating() {
    const modal = document.getElementById('ratingModal');
    const rating = parseInt(modal.dataset.selectedRating);
    const professorId = modal.dataset.professorId;
    
    if (!rating || rating < 1 || rating > 5) {
        alert('Por favor, selecione uma avaliação.');
        return;
    }
    
    // Close modal
    closeRatingModal();
    
    // Submit rating
    await submitRating(rating, professorId === '' ? null : professorId);
}

/**
 * Atualizar visualização após mudanças
 */
function atualizarVisualizacao() {
    if (professorSelecionado === null) {
        // Atualizar visualização da disciplina
        const avaliacoesDisciplina = AVALIACOES_DATA.filter(a => !a.professorId);
        avaliacoesDisciplinaGlobal = avaliacoesDisciplina;
        const stats = calcularStats(avaliacoesDisciplina);
        
        // Filtrar comentários da disciplina
        const comentariosDisciplina = COMENTARIOS_DATA.filter(c => !c.professorId);
        
        atualizarDisciplina(stats, comentariosDisciplina);
    } else {
        // Atualizar visualização do professor
        const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
        const avaliacoesProf = avaliacoesProfessores.filter(a => 
            String(a.professorId) === String(professorSelecionado)
        );
        const stats = calcularStats(avaliacoesProf);
        
        // Filtrar comentários do professor
        const comentariosProfessor = COMENTARIOS_DATA.filter(c => 
            String(c.professorId) === String(professorSelecionado)
        );
        
        // Encontrar índice do professor
        const profIndex = PROFESSORES_DATA.findIndex(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorSelecionado);
        });
        
        if (profIndex !== -1) {
            atualizarProfessor(profIndex, stats);
            
            // Atualizar lista de comentários
            const prof = PROFESSORES_DATA[profIndex];
            const profNome = prof?.nome || prof?.name || 'Professor';
            mostrarComentarios(comentariosProfessor, profNome);
        }
    }
    
    // ✅ Re-setup interactive stars after updating visualization
    setupInteractiveStars();
}

/**
 * Submeter rating (estrelas clicáveis)
 */
async function submitRating(rating, professorId = null) {
    
    if (rating < 1 || rating > 5) {
        alert('Nota inválida.');
        return;
    }
    
    console.log(`Submetendo rating: ${rating} estrelas para ${professorId ? 'professor' : 'disciplina'}`);
    
    try {
        const formData = new FormData();
        formData.append('nota', rating);
        formData.append('disciplinaId', CLASS_ID);
        if (professorId) {
            formData.append('professorId', professorId);
        }
        
        const response = await fetch('/api/avaliacao/rating', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao enviar avaliação: ' + errorText);
            return;
        }
        
        const result = await response.json();
        console.log('Rating salvo:', result);
        
        // ✅ Simplified: Just reload the page after successful rating
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao enviar rating:', error);
        alert('Erro ao enviar avaliação: ' + error.message);
        return null;
    }
}

/**
 * Votar em um comentário (upvote ou downvote)
 */
async function voteComment(comentarioId, isUpVote) {
    
    try {
		  const formData = new FormData();
   	  formData.append('isUpVote', isUpVote);
		  
		  console.log(`Votando no comentário ${comentarioId} - Upvote: ${isUpVote}`);

        const response = await fetch(`/api/comentarios/${comentarioId}/votar`, {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao votar: ' + errorText);
            return;
        }
        
        const result = await response.json();
        console.log('Voto registrado:', result);
        
        // Atualizar contadores no frontend - agora usando COMENTARIOS_DATA
        const comentario = COMENTARIOS_DATA.find(c => c.id === comentarioId);
        if (comentario) {
            comentario.upVotes = result.upVotes || 0;
            comentario.downVotes = result.downVotes || 0;
            
            // ✅ Atualizar estado de voto (hasVoted)
            // Se o backend retornar o novo estado de voto
            if (result.userVote !== undefined) {
                // Backend pode retornar 1, -1, ou 0
                if (result.userVote === 0) {
                    comentario.hasVoted = 0;
                } else {
                    comentario.hasVoted = result.userVote;
                }
            } else {
                // Se backend não retornar, inferir baseado no clique
                // Se clicou no mesmo voto, remove (null), senão define o voto
                if (comentario.hasVoted === (isUpVote ? 1 : -1)) {
                    comentario.hasVoted = 0; // Toggle off
                } else {
                    comentario.hasVoted = isUpVote ? 1 : -1; // Set new vote
                }
            }
				console.log('Estado atualizado do comentário após voto:', comentario);
            
            // Atualizar visualização
            if (professorSelecionado === null) {
                const comentariosDisciplina = COMENTARIOS_DATA.filter(c => !c.professorId);
                mostrarComentarios(comentariosDisciplina, null);
            } else {
                const comentariosProfessor = COMENTARIOS_DATA.filter(c => 
                    String(c.professorId) === String(professorSelecionado)
                );
                const prof = PROFESSORES_DATA.find(p => {
                    const profId = p.id || p.professorId || p.ID;
                    return String(profId) === String(professorSelecionado);
                });
                const profNome = prof?.nome || prof?.name || 'Professor';
                mostrarComentarios(comentariosProfessor, profNome);
            }
        }
        
    } catch (error) {
        console.error('Erro ao votar:', error);
        alert('Erro ao votar: ' + error.message);
    }
}

/**
 * Responder a um comentário
 */
function replyToComment(comentarioId) {
    // TODO: Implementar sistema de respostas aninhadas
    alert(`Funcionalidade de resposta será implementada em breve.\nComentário ID: ${comentarioId}`);
    console.log('Reply to comment:', comentarioId);
}

/**
 * Editar um comentário existente
 */
function editComment(comentarioId) {
    // Find the comment data
    const comentario = COMENTARIOS_DATA.find(c => c.id === comentarioId);
    
    if (!comentario) {
        alert('Comentário não encontrado.');
        return;
    }
    
    // Store editing state
    editingCommentId = comentarioId;
    editingCommentData = comentario;
    
    // Populate the editor with existing comment text
    document.getElementById('commentText').value = comentario.texto;
    
    // Convert existing files (arquivos) to File objects for selectedFiles
    // Since we can't create File objects from URLs directly, we'll fetch them as blobs
    selectedFiles = [];
    
    if (comentario.arquivos && comentario.arquivos.length > 0) {
        // Show a loading state while fetching files
        const fileList = document.getElementById('fileList');
        fileList.innerHTML = '<div class="loading-files">Carregando arquivos existentes...</div>';
        
        // Fetch all existing files
        const filePromises = comentario.arquivos.map(async (arquivo) => {
            try {
                const response = await fetch(`/api/arquivos/${arquivo.id}`);
                if (!response.ok) throw new Error('Failed to fetch file');
                
                const blob = await response.blob();
                // Create a File object from the blob
                const file = new File([blob], arquivo.nomeOriginal, { type: arquivo.tipoMime });
                return file;
            } catch (error) {
                console.error('Error fetching file:', arquivo.nomeOriginal, error);
                return null;
            }
        });
        
        Promise.all(filePromises).then(files => {
            selectedFiles = files.filter(f => f !== null);
            renderFileList();
            updateSubmitButton();
        });
    } else {
        renderFileList();
    }
    
    // Update submit button state
    updateSubmitButton();
    
    // Open the editor
    const editor = document.getElementById('commentEditor');
    if (!editor.classList.contains('show')) {
        toggleCommentEditor();
    } else {
        // If already open, just update the subtitle
        updateEditorSubtitle();
    }
    
    // Scroll to the top where the editor is
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
    
    // Focus on textarea after scroll
    setTimeout(() => {
        document.getElementById('commentText').focus();
    }, 500);
}

/**
 * Deletar um comentário
 */
async function deleteComment(comentarioId) {
    // Confirmação antes de deletar
    if (!confirm('Tem certeza que deseja deletar este comentário? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    try {
        console.log(`Deletando comentário ${comentarioId}...`);
        
        const response = await fetch(`/api/comentarios/${comentarioId}/delete`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao deletar comentário: ' + errorText);
            return;
        }
        
        console.log('Comentário deletado com sucesso');
        
        // ✅ Remover comentário do array global
        const indexGlobal = COMENTARIOS_DATA.findIndex(c => c.id === comentarioId);
        if (indexGlobal !== -1) {
            COMENTARIOS_DATA.splice(indexGlobal, 1);
        }
        
        // ✅ Atualizar visualização baseado no contexto atual
        if (professorSelecionado === null) {
            // Atualizar comentários da disciplina
            const comentariosDisciplina = COMENTARIOS_DATA.filter(c => !c.professorId);
            mostrarComentarios(comentariosDisciplina, null);
        } else {
            // Atualizar comentários do professor
            const comentariosProfessor = COMENTARIOS_DATA.filter(c => 
                String(c.professorId) === String(professorSelecionado)
            );
            
            // Encontrar nome do professor
            const profIndex = PROFESSORES_DATA.findIndex(p => {
                const profId = p.id || p.professorId || p.ID;
                return String(profId) === String(professorSelecionado);
            });
            
            const profNome = profIndex !== -1 
                ? (PROFESSORES_DATA[profIndex]?.nome || PROFESSORES_DATA[profIndex]?.name || 'Professor')
                : 'Professor';
            
            mostrarComentarios(comentariosProfessor, profNome);
        }
        
        // ✅ Feedback visual
        alert('Comentário deletado com sucesso!');
        
    } catch (error) {
        console.error('Erro ao deletar comentário:', error);
        alert('Erro ao deletar comentário: ' + error.message);
    }
}

/**
 * Escapar HTML para prevenir XSS em strings interpoladas
 */
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;',
        '`': '&#x60;',
        '\\': '&#x5C;'
    };
    return text.replace(/[&<>"'`\\]/g, char => map[char]);
}
